package groupId.Controllers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

/**
 * Global controller for serving static files and debugging.
 */
@RestController
@RequestMapping("/")
public class MainController {

    @NonNull
    @GetMapping(value = "/")
    public ResponseEntity<Resource> serveHomePage() {
        Resource resource = new ClassPathResource("static/index.html");
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(resource);
    }
}