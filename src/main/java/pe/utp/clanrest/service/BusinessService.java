package pe.utp.clanrest.service;

import java.math.*;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.utp.clanrest.entity.*;
import pe.utp.clanrest.repository.*;

@Service
@lombok.Getter
@RequiredArgsConstructor
@Transactional
public class BusinessService {
  public final ClientRepository clients;
  public final CatalogItemRepository catalog;
  public final ComponentRepository components;
  public final QuotationRepository quotes;
  public final QuoteLineRepository lines;
  public final EventRepository events;
  public final EventTaskRepository tasks;
  public final StockMovementRepository movements;
  public final PaymentRepository payments;
  public final AppUserRepository users;
  public final AuditEntryRepository audits;
  private final PasswordEncoder encoder;

  public String actor() {
    var a = SecurityContextHolder.getContext().getAuthentication();
    return a == null ? "DEMO" : a.getName();
  }

  public boolean admin() {
    var a = SecurityContextHolder.getContext().getAuthentication();
    return a != null
        && a.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_ADMIN"));
  }

  public void require(boolean condition, String message) {
    if (!condition) throw new IllegalArgumentException(message);
  }

  public void audit(String action, String entity, Long id, String detail) {
    var a = new AuditEntry();
    a.actor = actor();
    a.action = action;
    a.entityName = entity;
    a.entityId = id;
    a.detail = detail;
    audits.save(a);
  }

  public static BigDecimal money(BigDecimal n) {
    return n.setScale(2, RoundingMode.HALF_UP);
  }

  @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
  public Client saveClient(
      Long id,
      String name,
      String doc,
      String phone,
      String email,
      String preferences,
      boolean active) {
    require(
        name != null && !name.isBlank() && name.length() <= 120,
        "Nombre obligatorio, máximo 120 caracteres");
    require(
        email == null || email.isBlank() || email.matches("^[^@ ]+@[^@ ]+[.][^@ ]+$"),
        "Correo inválido");
    var c = id == null ? new Client() : clients.findById(id).orElseThrow();
    c.name = name.trim();
    c.documentNumber = doc;
    c.phone = phone;
    c.email = email;
    c.preferences = preferences;
    c.active = active;
    clients.save(c);
    audit("GUARDAR", "Client", c.id, c.name);
    return c;
  }

  @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
  public CatalogItem saveItem(
      Long id,
      String name,
      String kind,
      String unit,
      BigDecimal price,
      BigDecimal minimum,
      String description,
      boolean active) {
    require(!name.isBlank(), "Nombre obligatorio");
    require(Set.of("MENU", "PLATO", "INSUMO", "EXTRA", "PAQUETE").contains(kind), "Tipo inválido");
    require(price.signum() >= 0 && minimum.signum() >= 0, "Precio y mínimo deben ser positivos");
    require(
        price.scale() <= 2 && minimum.scale() <= 3,
        "Precio: máximo 2 decimales; mínimo: 3 decimales");
    var c = id == null ? new CatalogItem() : catalog.lockById(id).orElseThrow();
    require(id == null || c.kind.equals(kind), "El tipo no puede cambiar; cree un nuevo artículo");
    c.name = name;
    c.kind = kind;
    c.unit = unit;
    c.price = money(price);
    c.minimum = minimum;
    c.description = description;
    c.active = active;
    catalog.save(c);
    audit("GUARDAR", "CatalogItem", c.id, c.name);
    return c;
  }

  @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
  public void addComponent(Long parentId, Long ingredientId, BigDecimal qty) {
    var parent = catalog.findById(parentId).orElseThrow();
    var ingredient = catalog.findById(ingredientId).orElseThrow();
    require(
        qty.signum() > 0 && qty.scale() <= 3, "Cantidad por persona positiva, máximo 3 decimales");
    boolean allowed =
        (Set.of("MENU", "PAQUETE").contains(parent.kind) && ingredient.kind.equals("PLATO"))
            || (parent.kind.equals("PLATO") && ingredient.kind.equals("INSUMO"));
    require(allowed, "Menú/paquete contiene platos; plato contiene insumos");
    var c = new Component();
    c.parent = parent;
    c.ingredient = ingredient;
    c.quantity = qty;
    components.save(c);
    audit("RECETA", "Component", c.id, parent.name + " / " + ingredient.name);
  }

  @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
  public void removeComponent(Long id) {
    components.deleteById(id);
    audit("QUITAR", "Component", id, "Receta");
  }

  @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
  public Quotation createQuote(
      Long clientId,
      Long menuId,
      Long extraId,
      int extraQty,
      int people,
      LocalDateTime date,
      LocalDate valid,
      String location,
      String restrictions,
      BigDecimal discount,
      BigDecimal tax) {
    var client = clients.findById(clientId).orElseThrow();
    var menu = catalog.findById(menuId).orElseThrow();
    require(client.active && menu.active, "Cliente o menú inactivo");
    require(Set.of("MENU", "PAQUETE").contains(menu.kind), "Seleccione un menú o paquete");
    require(people > 0 && people <= 10000, "Asistentes entre 1 y 10000");
    require(date.isAfter(LocalDateTime.now()), "El evento debe ser futuro");
    require(!valid.isBefore(LocalDate.now()), "Vigencia no puede ser pasada");
    require(location != null && !location.isBlank(), "Lugar obligatorio");
    require(
        discount.signum() >= 0
            && discount.scale() <= 2
            && tax.scale() <= 2
            && tax.signum() >= 0
            && tax.compareTo(new BigDecimal("100")) <= 0,
        "Descuento o impuesto inválido");
    var q = new Quotation();
    q.client = client;
    q.attendees = people;
    q.eventDate = date;
    q.validUntil = valid;
    q.location = location;
    q.restrictions = restrictions;
    q.discount = money(discount);
    q.taxPercent = tax;
    quotes.save(q);
    q.code = "COT-" + String.format("%06d", q.id);
    addLine(q, menu, people);
    BigDecimal subtotal = menu.price.multiply(BigDecimal.valueOf(people));
    if (extraId != null) {
      var extra = catalog.findById(extraId).orElseThrow();
      require(extra.active && extra.kind.equals("EXTRA") && extraQty > 0, "Extra inválido");
      addLine(q, extra, extraQty);
      subtotal = subtotal.add(extra.price.multiply(BigDecimal.valueOf(extraQty)));
    }
    require(discount.compareTo(subtotal) <= 0, "Descuento mayor al subtotal");
    q.total =
        money(
            subtotal
                .subtract(discount)
                .multiply(BigDecimal.ONE.add(tax.divide(new BigDecimal("100")))));
    audit("CREAR", "Quotation", q.id, q.code);
    return q;
  }

  private void addLine(Quotation q, CatalogItem item, int qty) {
    var l = new QuoteLine();
    l.quotation = q;
    l.item = item;
    l.description = item.name;
    l.unitPrice = item.price;
    l.quantity = qty;
    lines.save(l);
  }

  @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
  public void changeQuote(Long id, String state) {
    var q = quotes.lockById(id).orElseThrow();
    require(Set.of("ENVIADA", "RECHAZADA").contains(state), "Estado inválido");
    require(Set.of("BORRADOR", "ENVIADA").contains(q.status), "Cotización cerrada");
    q.status = state;
    audit("ESTADO", "Quotation", id, state);
  }

  @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
  public Event approve(Long id) {
    var q = quotes.lockById(id).orElseThrow();
    var existing = events.findByQuotationId(id);
    if (existing.isPresent()) return existing.get();
    require(Set.of("BORRADOR", "ENVIADA").contains(q.status), "Cotización no aprobable");
    require(!q.validUntil.isBefore(LocalDate.now()), "Cotización vencida");
    require(q.eventDate.isAfter(LocalDateTime.now()), "Fecha del evento pasada");
    q.status = "APROBADA";
    var e = new Event();
    e.quotation = q;
    e.date = q.eventDate;
    e.location = q.location;
    e.responsible = actor();
    e.service = "Catering para " + q.attendees + " asistentes";
    events.save(e);
    audit("APROBAR_CONVERTIR", "Event", e.id, q.code);
    return e;
  }

  @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
  public void saveEvent(
      Long id,
      LocalDateTime date,
      String location,
      String responsible,
      String state,
      String service,
      String staff,
      String equipment,
      String notes) {
    var e = events.lockById(id).orElseThrow();
    require(
        date != null && !location.isBlank() && !responsible.isBlank(),
        "Fecha, lugar y responsable obligatorios");
    var transitions =
        Map.of(
            "CONFIRMADO",
            Set.of("CONFIRMADO", "EN_PREPARACION", "CANCELADO"),
            "EN_PREPARACION",
            Set.of("EN_PREPARACION", "EN_SERVICIO", "CANCELADO"),
            "EN_SERVICIO",
            Set.of("EN_SERVICIO", "CERRADO"),
            "CERRADO",
            Set.of("CERRADO"),
            "CANCELADO",
            Set.of("CANCELADO"));
    require(transitions.get(e.status).contains(state), "Transición de evento no permitida");
    require(!state.equals("CERRADO") || balance(e).signum() == 0, "Para cerrar, liquide el saldo");
    e.date = date;
    e.location = location;
    e.responsible = responsible;
    e.status = state;
    e.service = service;
    e.staff = staff;
    e.equipment = equipment;
    e.notes = notes;
    audit("ACTUALIZAR", "Event", id, state);
  }

  @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
  public void addTask(Long id, String description, String responsible, LocalDate due) {
    require(!description.isBlank() && !responsible.isBlank(), "Tarea y responsable obligatorios");
    var t = new EventTask();
    t.event = events.findById(id).orElseThrow();
    t.description = description;
    t.responsible = responsible;
    t.dueDate = due;
    tasks.save(t);
    audit("TAREA", "EventTask", t.id, description);
  }

  @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR','ALMACEN')")
  public void toggleTask(Long id) {
    var t = tasks.findById(id).orElseThrow();
    t.done = !t.done;
    audit("TAREA_ESTADO", "EventTask", id, Boolean.toString(t.done));
  }

  public record Requirement(CatalogItem item, BigDecimal quantity) {}

  public List<Requirement> requirements(Event event) {
    var totals = new LinkedHashMap<Long, Requirement>();
    for (var l : lines.findByQuotationId(event.quotation.id))
      expand(l.item, BigDecimal.valueOf(l.quantity), totals);
    return new ArrayList<>(totals.values());
  }

  private void expand(CatalogItem item, BigDecimal multiplier, Map<Long, Requirement> totals) {
    if (item.kind.equals("INSUMO")) {
      var prev = totals.get(item.id);
      totals.put(
          item.id,
          new Requirement(item, multiplier.add(prev == null ? BigDecimal.ZERO : prev.quantity)));
      return;
    }
    for (var c : components.findByParentId(item.id))
      expand(c.ingredient, multiplier.multiply(c.quantity), totals);
  }

  @PreAuthorize("hasAnyRole('ADMIN','ALMACEN')")
  public StockMovement move(
      Long itemId,
      Long eventId,
      String kind,
      BigDecimal quantity,
      String reason,
      boolean authorizeNegative) {
    var item = catalog.lockById(itemId).orElseThrow();
    require(item.kind.equals("INSUMO"), "Solo insumos tienen stock");
    require(Set.of("ENTRADA", "SALIDA", "AJUSTE").contains(kind), "Tipo inválido");
    require(!reason.isBlank(), "Motivo obligatorio");
    require(quantity.scale() <= 3, "Cantidad: máximo 3 decimales");
    require(
        !kind.equals("AJUSTE") ? quantity.signum() > 0 : quantity.signum() != 0,
        "Cantidad inválida");
    BigDecimal delta = kind.equals("SALIDA") ? quantity.negate() : quantity;
    BigDecimal next = item.stock.add(delta);
    require(
        next.signum() >= 0 || (authorizeNegative && admin()),
        "Stock insuficiente; autorización ADMIN requerida");
    var m = new StockMovement();
    m.item = item;
    m.event = eventId == null ? null : events.findById(eventId).orElseThrow();
    m.kind = kind;
    m.quantity = quantity;
    m.reason = reason;
    m.actor = actor();
    m.resultingStock = next;
    item.stock = next;
    movements.save(m);
    audit("MOVIMIENTO", "StockMovement", m.id, kind + " " + quantity + " " + item.name);
    return m;
  }

  public BigDecimal paid(Event e) {
    return payments.findByEventId(e.id).stream()
        .filter(p -> !p.voided)
        .map(p -> p.amount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public BigDecimal balance(Event e) {
    return money(e.quotation.total.subtract(paid(e)));
  }

  @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR')")
  public Payment pay(
      Long eventId, BigDecimal amount, LocalDate date, String method, String reference) {
    var e = events.lockById(eventId).orElseThrow();
    require(!e.status.equals("CANCELADO"), "Evento cancelado");
    require(
        amount.scale() <= 2 && amount.signum() > 0 && amount.compareTo(balance(e)) <= 0,
        "Monto inválido o supera saldo");
    require(!date.isAfter(LocalDate.now()), "Pago no puede ser futuro");
    require(
        Set.of("EFECTIVO", "TRANSFERENCIA", "YAPE", "PLIN", "TARJETA").contains(method),
        "Método inválido");
    var p = new Payment();
    p.event = e;
    p.amount = amount;
    p.date = date;
    p.method = method;
    p.reference = reference;
    p.actor = actor();
    payments.save(p);
    audit("PAGO", "Payment", p.id, amount.toPlainString());
    return p;
  }

  @PreAuthorize("hasRole('ADMIN')")
  public void voidPayment(Long id, String reason) {
    var p = payments.findById(id).orElseThrow();
    var e = events.lockById(p.event.id).orElseThrow();
    require(!reason.isBlank(), "Motivo de anulación obligatorio");
    require(!p.voided, "Pago ya anulado");
    p.voided = true;
    p.voidReason = reason;
    if (e.status.equals("CERRADO")) e.status = "EN_SERVICIO";
    audit("ANULAR_PAGO", "Payment", id, reason);
  }

  @PreAuthorize("hasRole('ADMIN')")
  public AppUser saveUser(
      Long id, String username, String fullName, String password, String role, boolean active) {
    require(Set.of("ADMIN", "COORDINADOR", "ALMACEN").contains(role), "Rol inválido");
    require(username.matches("[a-zA-Z0-9._-]{3,40}"), "Usuario inválido");
    var u = id == null ? new AppUser() : users.findById(id).orElseThrow();
    if (id != null && u.role.equals("ADMIN") && u.active && (!role.equals("ADMIN") || !active))
      require(users.countByRoleAndActiveTrue("ADMIN") > 1, "Debe conservar un ADMIN activo");
    require(
        !username.equals(actor()) || (role.equals("ADMIN") && active),
        "No puede desactivar o degradar su cuenta");
    if (id == null || !password.isBlank()) {
      require(password.length() >= 10, "Contraseña de al menos 10 caracteres");
      u.password = encoder.encode(password);
    }
    u.username = username;
    u.fullName = fullName;
    u.role = role;
    u.active = active;
    users.save(u);
    audit("USUARIO", "AppUser", u.id, username + " " + role);
    return u;
  }

  public List<String> alerts() {
    var result = new ArrayList<String>();
    for (var i : catalog.findByKindAndActiveTrue("INSUMO"))
      if (i.stock.compareTo(i.minimum) <= 0)
        result.add("Stock bajo: " + i.name + " (" + i.stock + " " + i.unit + ")");
    for (var e : events.findAll())
      if (!Set.of("CERRADO", "CANCELADO").contains(e.status)) {
        if (e.date.isBefore(LocalDateTime.now().plusDays(7)))
          result.add("Evento próximo o atrasado #" + e.id + " · " + e.date);
        if (balance(e).signum() > 0) result.add("Saldo pendiente #" + e.id + ": S/ " + balance(e));
      }
    for (var t : tasks.findAll())
      if (!t.done && t.dueDate.isBefore(LocalDate.now()))
        result.add("Tarea vencida: " + t.description);
    return result;
  }
}
