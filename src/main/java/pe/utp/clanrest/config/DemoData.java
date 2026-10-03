package pe.utp.clanrest.config;

import java.math.BigDecimal;
import java.time.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.utp.clanrest.entity.*;
import pe.utp.clanrest.service.BusinessService;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "clanrest.demo", havingValue = "true")
public class DemoData implements CommandLineRunner {
  private final BusinessService s;
  private final PasswordEncoder encoder;

  @Override
  @Transactional
  public void run(String... args) {
    if (s.getUsers().count() > 0) return;
    for (String role : new String[] {"ADMIN", "COORDINADOR", "ALMACEN"}) {
      var u = new AppUser();
      u.username = role.toLowerCase();
      u.fullName = "Demo " + role;
      u.role = role;
      u.password = encoder.encode("ClanRest2026!");
      s.getUsers().save(u);
    }
    var c = new Client();
    c.name = "Eventos del Norte Demo";
    c.documentNumber = "DEMO-001";
    c.phone = "000000000";
    c.email = "demo@example.com";
    c.preferences = "Opciones vegetarianas";
    s.getClients().save(c);
    var rice = item("Arroz", "INSUMO", "kg", "5.00", "80", "10");
    var chicken = item("Pollo", "INSUMO", "kg", "12.00", "5", "10");
    var dish = item("Arroz con pollo", "PLATO", "porción", "20.00", "0", "0");
    var menu = item("Menú norteño", "MENU", "persona", "35.00", "0", "0");
    item("Personal adicional", "EXTRA", "hora", "25.00", "0", "0");
    recipe(dish, rice, "0.12");
    recipe(dish, chicken, "0.20");
    recipe(menu, dish, "1");
    var q = new Quotation();
    q.client = c;
    q.code = "COT-DEMO-001";
    q.attendees = 50;
    q.eventDate = LocalDateTime.now().plusDays(5).withHour(13).withMinute(0);
    q.validUntil = LocalDate.now().plusDays(7);
    q.location = "Local de demostración Piura";
    q.restrictions = "5 porciones sin ají";
    q.total = new BigDecimal("1750.00");
    q.status = "APROBADA";
    s.getQuotes().save(q);
    var l = new QuoteLine();
    l.quotation = q;
    l.item = menu;
    l.description = menu.name;
    l.quantity = 50;
    l.unitPrice = menu.price;
    s.getLines().save(l);
    var e = new Event();
    e.quotation = q;
    e.date = q.eventDate;
    e.location = q.location;
    e.responsible = "coordinador";
    e.service = "Almuerzo corporativo";
    e.staff = "2 cocineros, 2 mozos";
    e.equipment = "Mesas y menaje";
    s.getEvents().save(e);
    var p = new Payment();
    p.event = e;
    p.amount = new BigDecimal("500.00");
    p.date = LocalDate.now();
    p.method = "TRANSFERENCIA";
    p.reference = "DEMO";
    p.actor = "DEMO";
    s.getPayments().save(p);
    var t = new EventTask();
    t.event = e;
    t.description = "Confirmar compra de pollo";
    t.responsible = "almacen";
    t.dueDate = LocalDate.now().plusDays(2);
    s.getTasks().save(t);
    s.audit("CARGA_DEMO", "Event", e.id, "Datos ficticios para aprendizaje");
  }

  private CatalogItem item(
      String name, String kind, String unit, String price, String stock, String minimum) {
    var i = new CatalogItem();
    i.name = name;
    i.kind = kind;
    i.unit = unit;
    i.price = new BigDecimal(price);
    i.stock = new BigDecimal(stock);
    i.minimum = new BigDecimal(minimum);
    return s.getCatalog().save(i);
  }

  private void recipe(CatalogItem parent, CatalogItem ingredient, String qty) {
    var c = new pe.utp.clanrest.entity.Component();
    c.parent = parent;
    c.ingredient = ingredient;
    c.quantity = new BigDecimal(qty);
    s.getComponents().save(c);
  }
}
