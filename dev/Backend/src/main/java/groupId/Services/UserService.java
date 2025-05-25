package groupId.Services;

import Exceptions.InternalErrors.ClientSideError;
import Exceptions.UserErrors.UserDataException;
import Persistence.Entities.UserEntity;
import Persistence.EntityMapper;
import Persistence.Repositories.UserRepository;
import User.User;
import groupId.DTO.Records.Requests.Commands.LoginDTO;
import groupId.DTO.Records.Requests.Commands.RegisterDTO;
import groupId.DTO.Records.Requests.Responses.LoginResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 16;

    private final UserRepository userRepository;

    @Autowired
    public UserService (UserRepository userRepository) {
        this.userRepository = userRepository;
        if(!userRepository.existsByUsername("admin")){
            try {
                UserEntity entity = new UserEntity("admin", "admin", "admin@admin.com", "admin12345");
                userRepository.save(entity);
            }
            catch (RuntimeException e){
                throw new RuntimeException("Fatal error occurred while initializing User Service: "+ e.getMessage());
            }
        }
    }

    /**
     * Registers a new user in the system by saving their details into the repository.
     * Ensures that the provided email and username are unique before registering.
     *
     * @param registerData an instance of {@code RegisterDTO} containing the user's registration details
     * @throws UserDataException if the email already exists in the system or the username is already taken.
     */
    public void registerUser (RegisterDTO registerData) throws UserDataException {
        int passwordLength = registerData.password().length();
        if(passwordLength > MAX_PASSWORD_LENGTH || passwordLength < MIN_PASSWORD_LENGTH)
            throw new UserDataException("Password should be between 8 and 16 in length.");

        UserEntity entity= new UserEntity(registerData.userName(),registerData.nickname(), registerData.email(), registerData.password());
        if(userRepository.existsByEmail(entity.getEmail()))
            throw new UserDataException("Email already exists");
        if(userRepository.existsByUsername(entity.getUsername()))
            throw new UserDataException("Username already exists");
        userRepository.save(entity);
    }
    public Optional<UserEntity> getUser(String id) {
        return userRepository.findById(UUID.fromString(id));
    }
    /**
     * Authenticates a user by validating the provided username and password.
     * Throws an exception if the username or password is invalid.
     *
     * @param loginData an instance of {@code LoginDTO} containing the username and password provided by the user
     * @throws UserDataException if the username does not exist or the password is incorrect
     */
    public LoginResponseDTO loginUser (LoginDTO loginData) throws UserDataException {
        UserEntity user = userRepository.findByUsername(loginData.userName())
                .orElseThrow(() -> new UserDataException("Invalid username or password."));
        if(!user.checkPassword(loginData.password()))
            throw new UserDataException("Invalid username or password.");
        else {
            return new LoginResponseDTO(user.getId().toString());
        }
    }
}
