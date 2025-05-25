package groupId.Controllers;

import Persistence.Repositories.UserRepository;
import groupId.DTO.Records.Requests.Commands.*;
import groupId.Services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import groupId.DTO.Records.Image.SolutionDTO;
import jakarta.validation.Valid;

/**
 * Global controller for serving static files and debugging.
 */
@RestController
@RequestMapping("/")
public class MainController {

    private final MainService mainService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired 
    public MainController (MainService mainService) {
        this.mainService = mainService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<Resource> serveHomePage() {
        Resource resource = new ClassPathResource("static/index.html");
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(resource);
    }
    

    
    @PostMapping("/solve")
    public ResponseEntity<SolutionDTO> solve(@Valid @RequestBody SolveCommandDTO input) throws Exception {
        SolutionDTO res = mainService.solve(input);
        return ResponseEntity.ok(res);
    }
    @GetMapping("/kafka-test")
    public ResponseEntity<String> solve() {
        mainService.testKafka();
        return ResponseEntity.ok("Test completed.");
    }
    @GetMapping("/test-connection")
    public String testConnection() {
        try {
            // Test query to fetch PostgreSQL version
            String postgresVersion = jdbcTemplate.queryForObject("SELECT version();", String.class);
            return "Connected to PostgreSQL: " + postgresVersion;
        } catch (Exception e) {
            e.printStackTrace();
            return "Connection failed: " + e.getMessage();
        }
    }

}