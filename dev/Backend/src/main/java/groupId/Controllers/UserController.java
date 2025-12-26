package groupId.Controllers;


import groupId.DTO.Records.Requests.Commands.LoginDTO;
import groupId.DTO.Records.Requests.Commands.RegisterDTO;
import groupId.DTO.Records.Requests.Responses.ConfirmationDTO;
import groupId.DTO.Records.Requests.Responses.LoginResponseDTO;
import groupId.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Tag(name = "User Operations", description = "Operations related to user management.")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
        summary = "Login user",
        description = "Authenticates a user with their credentials and returns a session token",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully authenticated",
                content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))
            )
        }
    )
    @NonNull
    @PostMapping("/session")
    public ResponseEntity<LoginResponseDTO> logIn(
        @NonNull
        @Valid
        @RequestBody
        @Parameter(
            description = "Login credentials",
            required = true,
            content = @Content(schema = @Schema(implementation = LoginDTO.class))
        ) LoginDTO data
    ) {
        return ResponseEntity.ok(userService.loginUser(data));
    }

    @Operation(
        summary = "Register new user",
        description = "Creates a new user account with the provided information",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully registered",
                content = @Content(schema = @Schema(implementation = ConfirmationDTO.class))
            )
        }
    )
    @NonNull
    @PostMapping
    public ResponseEntity<ConfirmationDTO> register(
        @NonNull
        @Valid
        @RequestBody
        @Parameter(
            description = "User registration details",
            required = true,
            content = @Content(schema = @Schema(implementation = RegisterDTO.class))
        ) RegisterDTO data
    ) {
        userService.registerUser(data);
        return ResponseEntity.ok(new ConfirmationDTO("Registration Successful."));
    }
}