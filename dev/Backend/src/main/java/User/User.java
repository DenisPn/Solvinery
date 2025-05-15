package User;



public class User {

    private final String username;

    private final String email;
    private String nickname;
    public User (String username,String email) {
        this.username = username;
        this.email = email;
        this.nickname= username;
    }
    public User (String username,String email,String nickname) {
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

    public String getUsername () {
        return username;
    }

    public String getEmail () {
        return email;
    }
}
