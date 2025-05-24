package User;


import java.util.UUID;

public class User {

    private final UUID id;
    private final String username;

    private final String email;

    private String nickname;

    public User (UUID id,String username,String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.nickname= username;
    }
    public User (UUID id,String username,String email,String nickname) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.nickname= nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername () {
        return username;
    }

    public String getEmail () {
        return email;
    }
}
