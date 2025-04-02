package Persistence.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;
@Entity
@Table(name = "users")
public class UserEntity {

    @Column(name = "username",nullable = false,
            unique = true)
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    public UserEntity () {
    }
    public UserEntity (String username) {
        this.username = username;
    }
    public void setUsername (String username) {
        this.username = username;
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
