package pe.utp.clanrest.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pe.utp.clanrest.repository.AppUserRepository;

/** Revoca permisos obsoletos en la siguiente solicitud tras desactivar o cambiar el rol. */
public class AccountStateFilter extends OncePerRequestFilter {
  private final AppUserRepository users;

  public AccountStateFilter(AppUserRepository users) {
    this.users = users;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
      var user = users.findByUsername(auth.getName());
      if (user.isEmpty()
          || !user.get().active
          || auth.getAuthorities().stream()
              .noneMatch(a -> a.getAuthority().equals("ROLE_" + user.get().role))) {
        SecurityContextHolder.clearContext();
        var session = request.getSession(false);
        if (session != null) session.invalidate();
        response.sendRedirect(request.getContextPath() + "/login?expired");
        return;
      }
    }
    chain.doFilter(request, response);
  }
}
