package pe.utp.clanrest;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pe.utp.clanrest.entity.*;
import pe.utp.clanrest.service.BusinessService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClanRestIntegrationTest {
  @Test
  void physicalDdlPreservesForeignKeys() throws Exception {
    String sql = java.nio.file.Files.readString(java.nio.file.Path.of("database/clanrest.sql"));
    sql =
        sql.replaceAll("(?m)^--.*$", "")
            .replaceAll("(?m)^CREATE DATABASE.*$", "")
            .replaceAll("(?m)^USE clanrest;.*$", "");
    try (var connection =
            java.sql.DriverManager.getConnection(
                "jdbc:h2:mem:ddl;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        var statement = connection.createStatement()) {
      for (var command : sql.split(";")) if (!command.isBlank()) statement.execute(command);
      try (var rows =
          statement.executeQuery(
              "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'")) {
        rows.next();
        assertThat(rows.getInt(1)).isEqualTo(11);
      }
      assertThatThrownBy(
              () ->
                  statement.execute(
                      "INSERT INTO components(parent_id,ingredient_id,quantity) VALUES(999,999,1)"))
          .isInstanceOf(java.sql.SQLException.class);
    }
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  void changedRoleRevokesOldSessionOnNextRequest() throws Exception {
    var user = s.getUsers().findByUsername("coordinador").orElseThrow();
    s.saveUser(user.id, user.username, user.fullName, "", "ALMACEN", true);
    mvc.perform(get("/quotes").with(user("coordinador").roles("COORDINADOR")))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login?expired"));
  }

  @Autowired BusinessService s;
  @Autowired MockMvc mvc;

  private Quotation quote() {
    return s.createQuote(
        s.getClients().findAll().getFirst().id,
        s.getCatalog().findByKindAndActiveTrue("MENU").getFirst().id,
        null,
        0,
        100,
        LocalDateTime.now().plusDays(30),
        LocalDate.now().plusDays(7),
        "Piura",
        "Sin ají",
        new BigDecimal("100.00"),
        new BigDecimal("18"));
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  void fullCycleAndHistoricalPrice() {
    var q = quote();
    assertThat(q.total).isEqualByComparingTo("4012.00");
    var e = s.approve(q.id);
    assertThat(s.approve(q.id).id).isEqualTo(e.id);
    assertThat(s.getEvents().findAll().stream().filter(x -> x.quotation.id.equals(q.id)).count())
        .isEqualTo(1);
    var menu = s.getCatalog().findByKindAndActiveTrue("MENU").getFirst();
    menu.price = new BigDecimal("99.00");
    s.getCatalog().save(menu);
    assertThat(s.getLines().findByQuotationId(q.id).getFirst().unitPrice)
        .isEqualByComparingTo("35.00");
    var p = s.pay(e.id, new BigDecimal("1000.00"), LocalDate.now(), "YAPE", "TEST");
    assertThat(s.balance(e)).isEqualByComparingTo("3012.00");
    s.voidPayment(p.id, "Registro duplicado");
    assertThat(s.balance(e)).isEqualByComparingTo("4012.00");
    assertThat(s.getAudits().findAll()).anyMatch(a -> a.action.equals("ANULAR_PAGO"));
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  void paymentRejectsOverpaymentAndFractionalCent() {
    var e = s.approve(quote().id);
    assertThatThrownBy(() -> s.pay(e.id, new BigDecimal("5000"), LocalDate.now(), "EFECTIVO", "X"))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> s.pay(e.id, new BigDecimal("1.001"), LocalDate.now(), "YAPE", "X"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  void expiredApprovalBlocked() {
    var q = quote();
    q.validUntil = LocalDate.now().minusDays(1);
    s.getQuotes().save(q);
    assertThatThrownBy(() -> s.approve(q.id)).hasMessageContaining("vencida");
  }

  @Test
  @WithMockUser(username = "almacen", roles = "ALMACEN")
  void stockCannotBecomeNegative() {
    var item = s.getCatalog().findByKindAndActiveTrue("INSUMO").getFirst();
    var before = item.stock;
    assertThatThrownBy(
            () -> s.move(item.id, null, "SALIDA", before.add(BigDecimal.ONE), "Prueba", true))
        .hasMessageContaining("Stock insuficiente");
    assertThat(s.getCatalog().findById(item.id).orElseThrow().stock).isEqualByComparingTo(before);
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  void movementAndRecipe() {
    var item = s.getCatalog().findByKindAndActiveTrue("INSUMO").getFirst();
    var before = item.stock;
    s.move(item.id, null, "ENTRADA", new BigDecimal("2"), "Compra", false);
    s.move(item.id, null, "SALIDA", BigDecimal.ONE, "Consumo", false);
    assertThat(item.stock).isEqualByComparingTo(before.add(BigDecimal.ONE));
    var req = s.requirements(s.getEvents().findAll().getFirst());
    assertThat(req).hasSize(2);
    assertThat(req)
        .anyMatch(
            r -> r.item().name.equals("Arroz") && r.quantity().compareTo(new BigDecimal("6")) == 0);
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  void invalidRecipeAndDiscountBlocked() {
    var menu = s.getCatalog().findByKindAndActiveTrue("MENU").getFirst();
    assertThatThrownBy(() -> s.addComponent(menu.id, menu.id, BigDecimal.ONE))
        .hasMessageContaining("contiene");
    assertThatThrownBy(
            () ->
                s.createQuote(
                    s.getClients().findAll().getFirst().id,
                    menu.id,
                    null,
                    0,
                    1,
                    LocalDateTime.now().plusDays(1),
                    LocalDate.now(),
                    "Piura",
                    "",
                    new BigDecimal("999"),
                    BigDecimal.ZERO))
        .hasMessageContaining("Descuento");
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  void eventCannotCloseWithBalance() {
    var e = s.approve(quote().id);
    s.saveEvent(
        e.id,
        e.date,
        e.location,
        "coordinador",
        "EN_PREPARACION",
        "servicio",
        "personal",
        "equipo",
        "");
    s.saveEvent(
        e.id,
        e.date,
        e.location,
        "coordinador",
        "EN_SERVICIO",
        "servicio",
        "personal",
        "equipo",
        "");
    assertThatThrownBy(
            () ->
                s.saveEvent(
                    e.id,
                    e.date,
                    e.location,
                    "coordinador",
                    "CERRADO",
                    "servicio",
                    "personal",
                    "equipo",
                    ""))
        .hasMessageContaining("saldo");
    s.pay(e.id, e.quotation.total, LocalDate.now(), "TRANSFERENCIA", "TOTAL");
    s.saveEvent(
        e.id, e.date, e.location, "coordinador", "CERRADO", "servicio", "personal", "equipo", "");
    assertThat(e.status).isEqualTo("CERRADO");
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  void cannotRemoveLastAdmin() {
    var u = s.getUsers().findByUsername("admin").orElseThrow();
    assertThatThrownBy(() -> s.saveUser(u.id, u.username, u.fullName, "", "ALMACEN", false))
        .hasMessageContaining("ADMIN");
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  void allAdminScreensRender() throws Exception {
    for (var path :
        new String[] {
          "/",
          "/clients",
          "/clients/new",
          "/clients/1",
          "/catalog",
          "/catalog/new",
          "/catalog/1",
          "/quotes",
          "/quotes/new",
          "/quotes/1",
          "/events",
          "/events/1",
          "/inventory",
          "/payments",
          "/alerts",
          "/reports",
          "/reports/export",
          "/users",
          "/users/new",
          "/users/1",
          "/audit"
        }) mvc.perform(get(path)).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "almacen", roles = "ALMACEN")
  void warehouseScreensAndDeniedFinancialAccess() throws Exception {
    for (var p : new String[] {"/", "/catalog", "/events", "/events/1", "/inventory", "/alerts"})
      mvc.perform(get(p)).andExpect(status().isOk());
    for (var p : new String[] {"/clients", "/quotes", "/payments", "/reports", "/users", "/audit"})
      mvc.perform(get(p)).andExpect(status().isForbidden());
    org.springframework.security.core.context.SecurityContextHolder.setContext(
        org.springframework.security.test.context.TestSecurityContextHolder.getContext());
    assertThatThrownBy(() -> s.approve(1L))
        .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
  }

  @Test
  @WithMockUser(username = "coordinador", roles = "COORDINADOR")
  void coordinatorCannotMoveStockOrManageUsers() throws Exception {
    mvc.perform(get("/users")).andExpect(status().isForbidden());
    org.springframework.security.core.context.SecurityContextHolder.setContext(
        org.springframework.security.test.context.TestSecurityContextHolder.getContext());
    assertThatThrownBy(() -> s.move(1L, null, "ENTRADA", BigDecimal.ONE, "Compra", false))
        .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    assertThatThrownBy(() -> s.voidPayment(1L, "X"))
        .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
  }

  @Test
  void anonymousRedirectAndRealLogin() throws Exception {
    mvc.perform(get("/events")).andExpect(status().is3xxRedirection());
    mvc.perform(get("/login")).andExpect(status().isOk());
    mvc.perform(
            post("/login")
                .with(csrf())
                .param("username", "admin")
                .param("password", "ClanRest2026!"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  void csrfAndClientSubmission() throws Exception {
    mvc.perform(post("/clients/save")).andExpect(status().isForbidden());
    mvc.perform(
            post("/clients/save")
                .with(csrf())
                .param("name", "Cliente prueba")
                .param("documentNumber", "TEST")
                .param("phone", "000")
                .param("email", "test@example.com")
                .param("preferences", "Sin gluten")
                .param("active", "true"))
        .andExpect(status().is3xxRedirection());
    assertThat(s.getClients().findAll()).anyMatch(c -> c.name.equals("Cliente prueba"));
  }

  @Test
  @WithMockUser(username = "admin", roles = "ADMIN")
  void exportEscapesSpreadsheetFormulas() throws Exception {
    var e = s.getEvents().findAll().getFirst();
    e.quotation.client.name = "=SUM(1,2)";
    s.getClients().save(e.quotation.client);
    var text =
        mvc.perform(get("/reports/export"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    assertThat(text).contains("'=SUM(1,2)");
  }
}
