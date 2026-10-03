package pe.utp.clanrest.entity;

import jakarta.persistence.*;
import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "event_tasks")
public class EventTask {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Version public Long version;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  public Event event;

  public String description;
  public String responsible;
  public LocalDate dueDate;
  public boolean done;
}
