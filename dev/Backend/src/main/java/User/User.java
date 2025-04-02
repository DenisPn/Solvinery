package User;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.springframework.context.annotation.Primary;

import java.util.UUID;

public class User {

    private final String username;

    private final String email;

    public User (String username,String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername () {
        return username;
    }

    public String getEmail () {
        return email;
    }
}
