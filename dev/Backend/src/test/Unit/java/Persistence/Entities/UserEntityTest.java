package Unit.java.Persistence.Entities;

import Persistence.Entities.UserEntity;
import Persistence.Repositories.UserRepository;
import Utilities.TestsConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureDataJpa
@ContextConfiguration(classes = {TestsConfiguration.class})
class UserEntityTest {

    @ParameterizedTest
    @ValueSource(strings = {"new_username", "another_user", "updated_user"})
    public void givenValidUser_whenUpdate_thenSuccess(String newUsername) {
        //given
        UserEntity user = new UserEntity("old_username");
        userRepository.save(user);

        //when
        user.setUsername(newUsername);
        userRepository.save(user);
        UserEntity updatedUser = userRepository.findById(user.getId()).orElse(null);

        //then
        assertNotNull(updatedUser);
        assertThat(updatedUser.getUsername()).isEqualTo(newUsername);
    }

    @Test
    public void givenDuplicateUsername_whenUpdate_thenThrowsException() {
        //given
        UserEntity user1 = new UserEntity("unique_user");
        UserEntity user2 = new UserEntity("another_user");
        userRepository.save(user1);
        userRepository.save(user2);

        //when
        user2.setUsername("unique_user");

        //then
        assertThrows(Exception.class, () -> userRepository.save(user2));
    }

    @ParameterizedTest
    @ValueSource(strings = {"delete_user", "to_remove", "temporary_user"})
    public void givenExistingUser_whenDelete_thenSuccess(String username) {
        //given
        UserEntity user = new UserEntity(username);
        userRepository.save(user);
        UUID userId = user.getId();

        //when
        userRepository.deleteById(userId);

        //then
        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    public void givenNonExistentUser_whenDelete_thenDoesNothing() {
        //given
        UUID nonExistentId = UUID.randomUUID();

        //when
        userRepository.deleteById(nonExistentId);

        //then
        assertThat(userRepository.findById(nonExistentId)).isEmpty();
    }

    @Autowired
    private UserRepository userRepository;

    @ParameterizedTest
    @ValueSource(strings = {"test_user","test_user-1","Smellma1996","BobTheBuilder",
    "Bob$Jones"})
    public void givenValidUser_whenSave_thenSuccess(String username) {
        //given
        UserEntity user = new UserEntity(username);
        userRepository.save(user);
        //when
        UserEntity foundUser = userRepository.findById(user.getId()).orElse(null);
        //then
        assertThat(foundUser).isNotNull();
        assertNotNull(foundUser);
        assertThat(foundUser.getUsername()).isEqualTo(username);
    }

    @Test
    public void givenNullUsername_whenSave_thenThrowsException() {
        //given
        UserEntity user = new UserEntity();
        user.setUsername(null);
        //when & then
        assertThrows(Exception.class, () -> userRepository.save(user));
    }

    @ParameterizedTest
    @ValueSource(strings = {"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
    "11111dsadsadf9dsyf8dsfhidhvosfeahiudsg8yfahfaifgoadfiuafgia"})
    public void givenLongUsername_whenSave_thenThrowsException(String username) {
        //given
        UserEntity user = new UserEntity(username);
        //when & then
        assertThrows(Exception.class, () -> userRepository.save(user));
    }
    @ParameterizedTest
    @ValueSource(strings = {""," ","   ",
            "                                                         ",
            "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n",
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t",
    })
    public void givenBlankUsername_whenSave_thenThrowsException(String username) {
        //given
        UserEntity user = new UserEntity(username);
        //when & then
        assertThrows(Exception.class, () -> userRepository.save(user));
    }
    @ParameterizedTest
    @ValueSource(strings = {"a","aa","-=","-1"})
    public void givenShortUsername_whenSave_thenThrowsException(String username) {
        //given
        UserEntity user = new UserEntity(username);
        //when & then
        assertThrows(Exception.class, () -> userRepository.save(user));
    }

    @Test
    public void givenDuplicateUsername_whenSave_thenThrowsException() {
        //given
        UserEntity user1 = new UserEntity("duplicateUser");
        UserEntity user2 = new UserEntity("duplicateUser");
        userRepository.save(user1);
        //when & then
        assertThrows(Exception.class, () -> userRepository.save(user2));
    }
    @AfterEach
    public void cleanDatabase() {
        userRepository.deleteAll();
    }

}