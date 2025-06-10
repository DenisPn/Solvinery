package groupId.Controllers;

import Persistence.Repositories.UserRepository;
import groupId.Services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

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

    @NonNull
    @GetMapping(value = "/")
    public ResponseEntity<Resource> serveHomePage() {
        Resource resource = new ClassPathResource("static/index.html");
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(resource);
    }


    @NonNull
    @GetMapping("/kafka-test")
    public ResponseEntity<String> solve() {
        mainService.testKafka();
        return ResponseEntity.ok("Test completed.");
    }
    @NonNull
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