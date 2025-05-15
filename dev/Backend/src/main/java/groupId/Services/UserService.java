package groupId.Services;

import Exceptions.UserErrors.UserDataException;
import Persistence.Entities.UserEntity;
import Persistence.Repositories.UserRepository;
import groupId.DTO.Records.Requests.Commands.LoginDTO;
import groupId.DTO.Records.Requests.Commands.RegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user in the system by saving their details into the repository.
     * Ensures that the provided email and username are unique before registering.
     *
     * @param registerData an instance of {@code RegisterDTO} containing the user's registration details
     * @throws UserDataException if the email already exists in the system or the username is already taken.
     */
    public void registerUser (RegisterDTO registerData) throws UserDataException {
        UserEntity entity= new UserEntity(registerData.userName(),registerData.nickname(), registerData.email(), registerData.password());
        if(userRepository.existsByEmail(entity.getEmail()))
            throw new UserDataException("Email already exists");
        if(userRepository.existsByUsername(entity.getUsername()))
            throw new UserDataException("Username already exists");
        userRepository.save(entity);
    }

    /**
     * Authenticates a user by validating the provided username and password.
     * Throws an exception if the username or password is invalid.
     *
     * @param loginData an instance of {@code LoginDTO} containing the username and password provided by the user
     * @throws UserDataException if the username does not exist or the password is incorrect
     */
    public void loginUser (LoginDTO loginData) throws UserDataException {
        UserEntity user = userRepository.findByUsername(loginData.userName())
                .orElseThrow(() -> new UserDataException("Invalid username or password."));
        if(!user.checkPassword(loginData.password()))
            throw new UserDataException("Invalid username or password.");
    }
}
