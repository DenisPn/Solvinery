package groupId;

import java.io.IOException;

import DTO.Records.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class Service implements ServiceInterface {
    private final UserController controller;

    @Autowired 
    public Service(UserController controller) {
        this.controller = controller;
    }
    
    @PostMapping("/images")
    public ResponseEntity<ImageDTO> createImage(@RequestBody CreateImageDTO zplFile) throws IOException {
        ImageDTO response = controller.createImage(zplFile);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/images")
    public ResponseEntity<Void> configureImage(@RequestBody ImageConfigDTO imgConfig){
        controller.configureImage(imgConfig);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/solve")
    public ResponseEntity<SolutionDTO> solve(@RequestBody ImageInputDTO input) {
        SolutionDTO res = controller.solve(input);
        return ResponseEntity.ok(res);
    }
}