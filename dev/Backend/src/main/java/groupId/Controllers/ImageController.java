package groupId.Controllers;

import groupId.DTO.Records.Requests.Commands.CreateImageFromFileDTO;
import groupId.DTO.Records.Requests.Commands.ImageConfigDTO;
import groupId.DTO.Records.Requests.Responses.CreateImageResponseDTO;
import groupId.Services.ImageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;
    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }
    @PostMapping("/images")
    public ResponseEntity<CreateImageResponseDTO> createImage(@Valid @RequestBody CreateImageFromFileDTO data) throws Exception {
        CreateImageResponseDTO response = imageService.createImageFromFile(data.code());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/images")
    public ResponseEntity<Void> configureImage(@Valid @RequestBody ImageConfigDTO imgConfig){
        imageService.overrideImage(imgConfig);
        return ResponseEntity.ok().build();
    }
}
