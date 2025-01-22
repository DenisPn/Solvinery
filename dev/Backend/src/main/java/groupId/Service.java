package groupId;

import java.io.IOException;

import DTO.Records.Requests.Commands.CreateImageFromFileDTO;
import DTO.Records.Requests.Commands.CreateImageFromPathDTO;
import DTO.Records.Requests.Commands.ImageConfigDTO;
import DTO.Records.Requests.Commands.SolveCommandDTO;
import DTO.Records.Image.SolutionDTO;
import DTO.Records.Requests.Responses.ImageResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@RestController
@RequestMapping("/")
public class Service implements ServiceInterface {
    private final UserController controller;

    @Autowired 
    public Service(UserController controller) {
        this.controller = controller;
    }

    @GetMapping(value = {"/", "/{path:^(?!api|static).*$}/**"})
    public ResponseEntity<Resource> serveHomePage() throws IOException {
        Resource resource = new ClassPathResource("static/index.html");
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(resource);
    }
    /*
    @Deprecated
    @PostMapping("/images/from-path")
    public ResponseEntity<ImageResponseDTO> createImage(@RequestBody CreateImageFromPathDTO path) throws IOException {
        ImageResponseDTO response = controller.createImageFromPath(path.path());
        return ResponseEntity.ok(response);
    }
    */
    @PostMapping("/images")
    public ResponseEntity<ImageResponseDTO> createImage(@RequestBody CreateImageFromFileDTO data) throws IOException {
        ImageResponseDTO response = controller.createImageFromFile(data.name(),data.code());
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/images")
    public ResponseEntity<Void> configureImage(@RequestBody ImageConfigDTO imgConfig){
        return ResponseEntity.badRequest().build();
    }
    @PostMapping("/solve")
    public ResponseEntity<SolutionDTO> solve(@RequestBody SolveCommandDTO input) {
        SolutionDTO res = controller.solve(input);
        return ResponseEntity.ok(res);
    }

}