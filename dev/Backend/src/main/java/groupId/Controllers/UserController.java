package groupId.Controllers;


import groupId.DTO.Records.Requests.Commands.LoginDTO;
import groupId.DTO.Records.Requests.Commands.RegisterDTO;
import groupId.DTO.Records.Requests.Responses.ConfirmationDTO;
import groupId.DTO.Records.Requests.Responses.LoginResponseDTO;
import groupId.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/user/login")
    public ResponseEntity<LoginResponseDTO> logIn(@Valid @RequestBody LoginDTO data) {
        return ResponseEntity.ok(userService.loginUser(data));
    }
    @PostMapping("/user")
    public ResponseEntity<ConfirmationDTO> register(@Valid @RequestBody RegisterDTO data) {
        userService.registerUser(data);
        return ResponseEntity.ok(new ConfirmationDTO("Registration Successful."));
    }
}
