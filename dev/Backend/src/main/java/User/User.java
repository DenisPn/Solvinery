package User;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.springframework.context.annotation.Primary;

import java.util.UUID;

public class User {

    private String username;

    public User (String username) {
        this.username = username;
    }

    public User () {
    }

    public String getUsername () {
        return username;
    }

}
