package groupId.Controllers;


import groupId.DTO.Records.Requests.Commands.LoginDTO;
import groupId.DTO.Records.Requests.Commands.RegisterDTO;
import groupId.DTO.Records.Requests.Responses.ConfirmationDTO;
import groupId.DTO.Records.Requests.Responses.LoginResponseDTO;
import groupId.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User-related operations.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @NonNull
    @PostMapping("/session")
    public ResponseEntity<LoginResponseDTO> logIn(@NonNull @Valid @RequestBody LoginDTO data) {
        return ResponseEntity.ok(userService.loginUser(data));
    }

    @NonNull
    @PostMapping
    public ResponseEntity<ConfirmationDTO> register(@NonNull @Valid @RequestBody RegisterDTO data) {
        userService.registerUser(data);
        return ResponseEntity.ok(new ConfirmationDTO("Registration Successful."));
    }
}
