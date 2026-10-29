package pe.utp.clanrest.controller;

import java.math.*;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.utp.clanrest.entity.*;
import pe.utp.clanrest.service.BusinessService;
import pe.utp.clanrest.service.ExcelReportService;

@Controller
@RequiredArgsConstructor
public class WebController {
  private final BusinessService s;
  private final ExcelReportService excelReports;

  @ModelAttribute
  public void common(Model m) {
    m.addAttribute("actor", s.actor());
    m.addAttribute("today", LocalDate.now());
  }

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @GetMapping("/")
  public String home(Model m) {
    m.addAttribute("clientCount", s.getClients().count());
    m.addAttribute("quoteCount", s.getQuotes().count());
    m.addAttribute("eventCount", s.getEvents().count());
    m.addAttribute("alerts", s.alerts());
    m.addAttribute(
        "events",
        s.getEvents().findAll().stream()
            .filter(e -> !Set.of("CANCELADO", "CERRADO").contains(e.status))
            .sorted(Comparator.comparing(e -> e.date))
            .limit(8)
            .toList());
    return "home";
  }

  @GetMapping("/clients")
  public String clients(Model m, @RequestParam(defaultValue = "") String search) {
    m.addAttribute(
        "clients",
        s.getClients().findAll().stream()
            .filter(c -> c.name.toLowerCase().contains(search.toLowerCase()))
            .toList());
    m.addAttribute("search", search);
    return "clients";
  }

  @GetMapping("/clients/new")
  public String clientNew(Model m) {
    m.addAttribute("c", new Client());
    return "client-form";
  }

  @GetMapping("/clients/{id}")
  public String client(Model m, @PathVariable Long id) {
    m.addAttribute("c", s.getClients().findById(id).orElseThrow());
    m.addAttribute(
        "history", s.getQuotes().findAll().stream().filter(q -> q.client.id.equals(id)).toList());
    return "client-form";
  }

  @PostMapping("/clients/save")
  public String clientSave(
      @RequestParam(required = false) Long id,
      @RequestParam String name,
      @RequestParam String documentNumber,
      @RequestParam String phone,
      @RequestParam String email,
      @RequestParam String preferences,
      @RequestParam(defaultValue = "false") boolean active) {
    s.saveClient(id, name, documentNumber, phone, email, preferences, active);
    return "redirect:/clients";
  }

  @GetMapping("/catalog")
  public String catalog(Model m) {
    m.addAttribute("items", s.getCatalog().findAll());
    return "catalog";
  }

  @GetMapping("/catalog/new")
  public String itemNew(Model m) {
    m.addAttribute("i", new CatalogItem());
    m.addAttribute("components", List.of());
    m.addAttribute("ingredients", s.getCatalog().findAll());
    return "item-form";
  }

  @GetMapping("/catalog/{id}")
  public String item(Model m, @PathVariable Long id) {
    m.addAttribute("i", s.getCatalog().findById(id).orElseThrow());
    m.addAttribute("components", s.getComponents().findByParentId(id));
    m.addAttribute("ingredients", s.getCatalog().findAll());
    return "item-form";
  }

  @PostMapping("/catalog/save")
  public String itemSave(
      @RequestParam(required = false) Long id,
      @RequestParam String name,
      @RequestParam String kind,
      @RequestParam String unit,
      @RequestParam BigDecimal price,
      @RequestParam BigDecimal minimum,
      @RequestParam String description,
      @RequestParam(defaultValue = "false") boolean active) {
    var i = s.saveItem(id, name, kind, unit, price, minimum, description, active);
    return "redirect:/catalog/" + i.id;
  }

  @PostMapping("/catalog/component")
  public String component(
      @RequestParam Long parentId,
      @RequestParam Long ingredientId,
      @RequestParam BigDecimal quantity) {
    s.addComponent(parentId, ingredientId, quantity);
    return "redirect:/catalog/" + parentId;
  }

  @PostMapping("/catalog/component/{id}/remove")
  public String removeComponent(@PathVariable Long id, @RequestParam Long parentId) {
    s.removeComponent(id);
    return "redirect:/catalog/" + parentId;
  }

  @GetMapping("/quotes")
  public String quotes(Model m, @RequestParam(defaultValue = "") String state) {
    m.addAttribute(
        "quotes",
        s.getQuotes().findAll().stream()
            .filter(q -> state.isEmpty() || q.status.equals(state))
            .sorted(Comparator.comparing((Quotation q) -> q.id).reversed())
            .toList());
    return "quotes";
  }

  @GetMapping("/quotes/new")
  public String newQuote(Model m) {
    m.addAttribute("clients", s.getClients().findAll().stream().filter(c -> c.active).toList());
    m.addAttribute(
        "menus",
        s.getCatalog().findAll().stream()
            .filter(i -> i.active && Set.of("MENU", "PAQUETE").contains(i.kind))
            .toList());
    m.addAttribute("extras", s.getCatalog().findByKindAndActiveTrue("EXTRA"));
    return "quote-form";
  }

  @PostMapping("/quotes/save")
  public String quoteSave(
      @RequestParam Long clientId,
      @RequestParam Long menuId,
      @RequestParam(required = false) Long extraId,
      @RequestParam(defaultValue = "0") int extraQty,
      @RequestParam int people,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate valid,
      @RequestParam String location,
      @RequestParam String restrictions,
      @RequestParam BigDecimal discount,
      @RequestParam BigDecimal tax) {
    var q =
        s.createQuote(
            clientId,
            menuId,
            extraId,
            extraQty,
            people,
            date,
            valid,
            location,
            restrictions,
            discount,
            tax);
    return "redirect:/quotes/" + q.id;
  }

  @GetMapping("/quotes/{id}")
  public String quote(Model m, @PathVariable Long id) {
    var q = s.getQuotes().findById(id).orElseThrow();
    m.addAttribute("q", q);
    m.addAttribute("lines", s.getLines().findByQuotationId(id));
    m.addAttribute("event", s.getEvents().findByQuotationId(id).orElse(null));
    return "quote-detail";
  }

  @PostMapping("/quotes/{id}/approve")
  public String approve(@PathVariable Long id) {
    var e = s.approve(id);
    return "redirect:/events/" + e.id;
  }

  @PostMapping("/quotes/{id}/state")
  public String state(@PathVariable Long id, @RequestParam String state) {
    s.changeQuote(id, state);
    return "redirect:/quotes/" + id;
  }

  @GetMapping("/events")
  public String events(
      Model m,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @RequestParam(defaultValue = "") String state,
      @RequestParam(defaultValue = "") String responsible,
      @RequestParam(required = false) String month) {
    var all =
        s.getEvents().findAll().stream()
            .filter(
                e ->
                    (from == null || !e.date.toLocalDate().isBefore(from))
                        && (to == null || !e.date.toLocalDate().isAfter(to))
                        && (state.isEmpty() || e.status.equals(state))
                        && (responsible.isEmpty()
                            || e.responsible.toLowerCase().contains(responsible.toLowerCase())))
            .sorted(Comparator.comparing(e -> e.date))
            .toList();
    YearMonth ym = month == null ? YearMonth.now() : YearMonth.parse(month);
    var days = new ArrayList<Map<String, Object>>();
    for (int d = 1; d <= ym.lengthOfMonth(); d++) {
      var date = ym.atDay(d);
      days.add(
          Map.of(
              "date",
              date,
              "day",
              d,
              "events",
              all.stream().filter(e -> e.date.toLocalDate().equals(date)).toList()));
    }
    m.addAttribute("events", all);
    m.addAttribute("days", days);
    m.addAttribute("month", ym.toString());
    m.addAttribute("offset", ym.atDay(1).getDayOfWeek().getValue());
    return "events";
  }

  @GetMapping("/events/{id}")
  public String event(Model m, @PathVariable Long id) {
    var e = s.getEvents().findById(id).orElseThrow();
    m.addAttribute("e", e);
    m.addAttribute("tasks", s.getTasks().findByEventId(id));
    m.addAttribute("requirements", s.requirements(e));
    m.addAttribute("balance", s.balance(e));
    m.addAttribute("payments", s.getPayments().findByEventId(id));
    return "event-detail";
  }

  @PostMapping("/events/{id}/save")
  public String saveEvent(
      @PathVariable Long id,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
      @RequestParam String location,
      @RequestParam String responsible,
      @RequestParam String state,
      @RequestParam String service,
      @RequestParam String staff,
      @RequestParam String equipment,
      @RequestParam String notes) {
    s.saveEvent(id, date, location, responsible, state, service, staff, equipment, notes);
    return "redirect:/events/" + id;
  }

  @PostMapping("/events/{id}/tasks")
  public String task(
      @PathVariable Long id,
      @RequestParam String description,
      @RequestParam String responsible,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate due) {
    s.addTask(id, description, responsible, due);
    return "redirect:/events/" + id;
  }

  @PostMapping("/tasks/{id}/toggle")
  public String toggle(@PathVariable Long id) {
    var event = s.getTasks().findById(id).orElseThrow().event.id;
    s.toggleTask(id);
    return "redirect:/events/" + event;
  }

  @GetMapping("/inventory")
  public String inventory(Model m) {
    m.addAttribute("items", s.getCatalog().findByKindAndActiveTrue("INSUMO"));
    m.addAttribute("events", s.getEvents().findAll());
    m.addAttribute("movements", s.getMovements().findAllByOrderByIdDesc());
    return "inventory";
  }

  @PostMapping("/inventory/move")
  public String move(
      @RequestParam Long itemId,
      @RequestParam(required = false) Long eventId,
      @RequestParam String kind,
      @RequestParam BigDecimal quantity,
      @RequestParam String reason,
      @RequestParam(defaultValue = "false") boolean authorizeNegative) {
    s.move(itemId, eventId, kind, quantity, reason, authorizeNegative);
    return "redirect:/inventory";
  }

  @GetMapping("/payments")
  public String payments(Model m) {
    var events = s.getEvents().findAll();
    m.addAttribute("events", events);
    var balances = new HashMap<Long, BigDecimal>();
    events.forEach(e -> balances.put(e.id, s.balance(e)));
    m.addAttribute("balances", balances);
    m.addAttribute("payments", s.getPayments().findAll());
    return "payments";
  }

  @PostMapping("/payments/save")
  public String pay(
      @RequestParam Long eventId,
      @RequestParam BigDecimal amount,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam String method,
      @RequestParam String reference) {
    s.pay(eventId, amount, date, method, reference);
    return "redirect:/payments";
  }

  @PostMapping("/payments/{id}/void")
  public String voidPay(@PathVariable Long id, @RequestParam String reason) {
    s.voidPayment(id, reason);
    return "redirect:/payments";
  }

  @GetMapping("/alerts")
  public String alerts(Model m) {
    m.addAttribute("alerts", s.alerts());
    return "alerts";
  }

  private List<Event> reportEvents(LocalDate from, LocalDate to) {
    return s.getEvents().findAll().stream()
        .filter(
            e ->
                (from == null || !e.date.toLocalDate().isBefore(from))
                    && (to == null || !e.date.toLocalDate().isAfter(to)))
        .toList();
  }

  @GetMapping("/reports")
  public String reports(
      Model m,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    var ev = reportEvents(from, to);
    m.addAttribute("events", ev);
    m.addAttribute("from", from);
    m.addAttribute("to", to);
    m.addAttribute(
        "sales",
        ev.stream()
            .filter(e -> !e.status.equals("CANCELADO"))
            .map(e -> e.quotation.total)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
    m.addAttribute("collected", ev.stream().map(s::paid).reduce(BigDecimal.ZERO, BigDecimal::add));
    m.addAttribute(
        "pending",
        ev.stream()
            .filter(e -> !e.status.equals("CANCELADO"))
            .map(s::balance)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
    var counts = new LinkedHashMap<String, Long>();
    for (String st : List.of("BORRADOR", "ENVIADA", "APROBADA", "RECHAZADA"))
      counts.put(
          st,
          s.getQuotes().findAll().stream()
              .filter(
                  q ->
                      q.status.equals(st)
                          && (from == null || !q.created.isBefore(from))
                          && (to == null || !q.created.isAfter(to)))
              .count());
    m.addAttribute("counts", counts);
    m.addAttribute(
        "movements",
        s.getMovements().findAllByOrderByIdDesc().stream()
            .filter(
                x ->
                    (from == null || !x.timestamp.toLocalDate().isBefore(from))
                        && (to == null || !x.timestamp.toLocalDate().isAfter(to)))
            .toList());
    return "reports";
  }

  private String csv(String value) {
    String v = value == null ? "" : value;
    if (v.matches("^[=+@-].*")) v = "'" + v;
    return "\"" + v.replace("\"", "\"\"") + "\"";
  }

  @GetMapping("/reports/export")
  public ResponseEntity<String> export(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    var b = new StringBuilder("Evento,Fecha,Cliente,Estado,Total,Cobrado,Saldo\r\n");
    for (var e : reportEvents(from, to))
      b.append(e.id)
          .append(',')
          .append(e.date)
          .append(',')
          .append(csv(e.quotation.client.name))
          .append(',')
          .append(e.status)
          .append(',')
          .append(e.quotation.total)
          .append(',')
          .append(s.paid(e))
          .append(',')
          .append(s.balance(e))
          .append("\r\n");
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clanrest-eventos.csv")
        .contentType(new MediaType("text", "csv", java.nio.charset.StandardCharsets.UTF_8))
        .body(b.toString());
  }

  /** Exporta los eventos del periodo a un libro XLSX protegido contra fórmulas inyectadas. */
  @GetMapping("/reports/export/xlsx")
  public ResponseEntity<byte[]> exportXlsx(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to)
      throws java.io.IOException {
    var rows =
        reportEvents(from, to).stream()
            .map(
                e ->
                    new ExcelReportService.EventReportRow(
                        e.id,
                        e.date,
                        e.quotation.client.name,
                        e.status,
                        e.quotation.total,
                        s.paid(e),
                        s.balance(e)))
            .toList();
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clanrest-eventos.xlsx")
        .contentType(
            MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(excelReports.generate(rows));
  }

  @GetMapping("/users")
  public String users(Model m) {
    m.addAttribute("users", s.getUsers().findAll());
    return "users";
  }

  @GetMapping("/users/new")
  public String userNew(Model m) {
    var u = new AppUser();
    u.role = "COORDINADOR";
    m.addAttribute("u", u);
    return "user-form";
  }

  @GetMapping("/users/{id}")
  public String user(Model m, @PathVariable Long id) {
    m.addAttribute("u", s.getUsers().findById(id).orElseThrow());
    return "user-form";
  }

  @PostMapping("/users/save")
  public String userSave(
      @RequestParam(required = false) Long id,
      @RequestParam String username,
      @RequestParam String fullName,
      @RequestParam String password,
      @RequestParam String role,
      @RequestParam(defaultValue = "false") boolean active) {
    s.saveUser(id, username, fullName, password, role, active);
    return "redirect:/users";
  }

  @GetMapping("/audit")
  public String audit(Model m) {
    m.addAttribute("entries", s.getAudits().findTop200ByOrderByIdDesc());
    return "audit";
  }
}
