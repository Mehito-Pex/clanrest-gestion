package pe.utp.clanrest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class ErrorAdvice {
  @ExceptionHandler({
    IllegalArgumentException.class,
    java.util.NoSuchElementException.class,
    org.springframework.dao.DataIntegrityViolationException.class,
    org.springframework.dao.OptimisticLockingFailureException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String business(Exception e, Model m) {
    m.addAttribute(
        "message",
        e instanceof IllegalArgumentException
            ? e.getMessage()
            : "Registro inexistente, duplicado o actualizado por otro usuario. Revise los datos.");
    return "business-error";
  }
}
