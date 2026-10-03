package pe.utp.clanrest.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import pe.utp.clanrest.entity.AuditEntry;
import pe.utp.clanrest.repository.AuditEntryRepository;

@Component
@RequiredArgsConstructor
public class LoginAudit {
  private final AuditEntryRepository repo;

  @EventListener
  public void login(AuthenticationSuccessEvent event) {
    var a = new AuditEntry();
    a.actor = event.getAuthentication().getName();
    a.action = "LOGIN";
    a.entityName = "AppUser";
    a.detail = "Inicio de sesión";
    repo.save(a);
  }
}
