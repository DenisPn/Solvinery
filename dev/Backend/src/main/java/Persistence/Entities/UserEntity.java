package Persistence.Entities;

import Persistence.Entities.Image.ImageEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "username",nullable = false,
            unique = true)
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @Size(min = 3, max = 20, message = "Nickname must be between 3 and 20 characters")
    @NotBlank(message = "Nickname cannot be blank")
    private String nickname;

    @Column(name ="email", nullable = false, unique = true)
    @Email(message = "Email is not valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @Column(name = "password",nullable = false)
    @NotBlank(message = "Invalid password")
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ImageEntity> images = new HashSet<>();

    public UserEntity () {
    }
    public UserEntity (String username,String email) {
        this.username = username;
        this.email = email;
        this.password = null;
    }
    public UserEntity (String username,String nickname,String email,
                       String rawPassword) {
        this.username = username;
        this.email = email;
        if(nickname == null)
            this.nickname = username;
        else
            this.nickname = nickname;
        setPassword(rawPassword);

    }
    public UserEntity (String username,String email,
                       String rawPassword) {
        this.username = username;
        this.email = email;
        this.nickname = username;
        setPassword(rawPassword);

    }

    public Set<ImageEntity> getImages() {
        return images;
    }
    public void addImage(ImageEntity imageEntity){
        images.add(imageEntity);
    }
    public void removeImage(ImageEntity imageEntity){
        images.remove(imageEntity);
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

    public String getEmail () {
        return email;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    /**
     * Hashes the password and saves it in the entity
     * @param rawPassword Raw, unencrypted password
     */
    public void setPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.length() > 16 || rawPassword.length() < 8 || rawPassword.isBlank()) {
            this.password = null; //set password as an invalid null to throw error on save, not here.
        }
        else {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(rawPassword.getBytes());
                this.password = Base64.getEncoder().encodeToString(hash);

            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Critical error during user creation: "+e);
            }
        }
    }

    /**
     * Checks if the provided raw password matches the hashed password stored in the entity.
     *
     * @param rawPassword the plain text password to be checked
     * @return true if the raw password matches the stored password, false otherwise
     */
    public boolean checkPassword(String rawPassword) {
        if( rawPassword == null)
            return this.password == null;
        else
        {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(rawPassword.getBytes());
                String hashStr = Base64.getEncoder().encodeToString(hash);
                return this.password.equals(hashStr);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Critical error during user creation: "+e);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password);
    }
}
