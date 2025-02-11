package groupId;

import java.io.IOException;
import java.util.List;

import DTO.Records.Requests.Commands.CreateImageFromFileDTO;
import DTO.Records.Requests.Commands.CreateImageFromPathDTO;
import DTO.Records.Requests.Commands.ImageConfigDTO;
import DTO.Records.Requests.Commands.SolveCommandDTO;
import DTO.Records.Image.ImageDTO;
import DTO.Records.Image.SolutionDTO;
import DTO.Records.Model.ModelData.InputDTO;
import DTO.Records.Requests.Responses.CreateImageResponseDTO;
import DTO.Records.Requests.Responses.ImageResponseDTO;

import jakarta.validation.Valid;
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

    @GetMapping("images/{id}/inputs")
    public ResponseEntity<InputDTO> getImageInput(@PathVariable("id") String imageId) throws Exception {
        InputDTO res = controller.loadLastInput(imageId);
        return ResponseEntity.ok(res);
    }
}