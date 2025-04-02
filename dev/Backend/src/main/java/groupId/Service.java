package groupId;

import java.io.IOException;

import Persistence.Entities.UserEntity;
import Persistence.Repositories.UserRepository;
import User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import groupId.DTO.Records.Image.SolutionDTO;
import groupId.DTO.Records.Model.ModelData.InputDTO;
import groupId.DTO.Records.Requests.Commands.CreateImageFromFileDTO;
import groupId.DTO.Records.Requests.Commands.ImageConfigDTO;
import groupId.DTO.Records.Requests.Commands.SolveCommandDTO;
import groupId.DTO.Records.Requests.Responses.CreateImageResponseDTO;
import jakarta.validation.Valid;
import org.springframework.jdbc.core.JdbcTemplate;


@RestController
@RequestMapping("/")
public class Service implements ServiceInterface {

    private final UserController controller;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired 
    public Service(UserController controller) {
        this.controller = controller;
    }

    @GetMapping(value = {"/"/*, "/{path:^(?!api|static).*$}/**"*/})
    public ResponseEntity<Resource> serveHomePage() throws IOException {
        Resource resource = new ClassPathResource("static/index.html");
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(resource);
    }
    
    @PostMapping("/images")
    public ResponseEntity<CreateImageResponseDTO> createImage(@Valid @RequestBody CreateImageFromFileDTO data) throws Exception {
        CreateImageResponseDTO response = controller.createImageFromFile(data.code());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/images")
    public ResponseEntity<Void> configureImage(@Valid @RequestBody ImageConfigDTO imgConfig){
        controller.overrideImage(imgConfig);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/solve")
    public ResponseEntity<SolutionDTO> solve(@Valid @RequestBody SolveCommandDTO input) throws Exception {
        SolutionDTO res = controller.solve(input);
        return ResponseEntity.ok(res);
    }

    /*@GetMapping("images/{id}/inputs")
    public ResponseEntity<InputDTO> loadImageInput(@PathVariable("id") String imageId) throws Exception {
        InputDTO res = controller.loadLastInput(imageId);
        return ResponseEntity.ok(res);
    }*/
    @GetMapping("/test-connection")
    public String testConnection() {
        try {
            // Test query to fetch PostgreSQL version
            String postgresVersion = jdbcTemplate.queryForObject("SELECT version();", String.class);
            userRepository.save(new UserEntity("test_user-2","email@email.com","mypass123"));
            return "Connected to PostgreSQL: " + postgresVersion;
        } catch (Exception e) {
            e.printStackTrace();
            return "Connection failed: " + e.getMessage();
        }
    }

}