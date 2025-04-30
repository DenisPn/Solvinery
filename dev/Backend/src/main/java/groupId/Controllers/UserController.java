package groupId.Controllers;


import groupId.DTO.Records.Requests.Commands.LoginDTO;
import groupId.DTO.Records.Requests.Commands.RegisterDTO;
import groupId.DTO.Records.Requests.Responses.ConfirmationDTO;
import groupId.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/login")
    public ResponseEntity<ConfirmationDTO> logIn(@Valid @RequestBody LoginDTO data) {
        userService.loginUser(data);
        return ResponseEntity.ok(new ConfirmationDTO("Registration Successful."));
    }
    @PostMapping("/register")
    public ResponseEntity<ConfirmationDTO> register(@Valid @RequestBody RegisterDTO data) {
        userService.registerUser(data);
        return ResponseEntity.ok(new ConfirmationDTO("Registration Successful."));
    }
}
