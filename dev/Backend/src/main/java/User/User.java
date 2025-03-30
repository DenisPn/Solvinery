package User;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.springframework.context.annotation.Primary;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Column(name = "username",nullable = false,
            unique = true)
    @Size(min = 3, max = 20)
    private String username;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    public User (String username) {
        this.username = username;
    }

    public User () {
    }

    public String getUsername () {
        return username;
    }

    public UUID getId () {
        return id;
    }
    public String getIdString () {
        return id == null ? null : id.toString();
    }

}
