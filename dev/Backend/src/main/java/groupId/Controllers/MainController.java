package groupId.Controllers;

import java.io.IOException;

import Persistence.Entities.UserEntity;
import Persistence.Repositories.UserRepository;
import groupId.DTO.Records.Requests.Commands.*;
import groupId.DTO.Records.Requests.Responses.ConfirmationDTO;
import groupId.ServiceInterface;
import groupId.Services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import groupId.DTO.Records.Image.SolutionDTO;
import groupId.DTO.Records.Requests.Responses.CreateImageResponseDTO;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/")
public class MainController {

    private final MainService controller;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired 
    public MainController (MainService controller) {
        this.controller = controller;
    }

    @GetMapping(value = {"/"/*, "/{path:^(?!api|static).*$}/**"*/})
    public ResponseEntity<Resource> serveHomePage() throws IOException {
        Resource resource = new ClassPathResource("static/index.html");
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(resource);
    }
    

    
    @PostMapping("/solve")
    public ResponseEntity<SolutionDTO> solve(@Valid @RequestBody SolveCommandDTO input) throws Exception {
        SolutionDTO res = controller.solve(input);
        return ResponseEntity.ok(res);
    }
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