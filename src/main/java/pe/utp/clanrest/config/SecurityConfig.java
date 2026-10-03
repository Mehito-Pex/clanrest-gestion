package pe.utp.clanrest.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pe.utp.clanrest.repository.AppUserRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService users(AppUserRepository repo) {
    return name -> {
      var u =
          repo.findByUsername(name)
              .orElseThrow(() -> new UsernameNotFoundException("Usuario inválido"));
      return User.withUsername(u.username)
          .password(u.password)
          .roles(u.role)
          .disabled(!u.active)
          .build();
    };
  }

  @Bean
  SecurityFilterChain security(HttpSecurity http, AppUserRepository repo) throws Exception {
    return http.addFilterAfter(
            new AccountStateFilter(repo),
            org.springframework.security.web.context.SecurityContextHolderFilter.class)
        .authorizeHttpRequests(
            a ->
                a.requestMatchers("/login", "/css/**", "/vendor/**", "/error")
                    .permitAll()
                    .requestMatchers("/users/**", "/audit/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/clients/**", "/quotes/**", "/payments/**", "/reports/**")
                    .hasAnyRole("ADMIN", "COORDINADOR")
                    .anyRequest()
                    .authenticated())
        .formLogin(f -> f.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
        .logout(l -> l.logoutSuccessUrl("/login?logout"))
        .build();
  }
}
