package groupId.Controllers;

import groupId.DTO.Records.Image.ImageDTO;
import groupId.DTO.Records.Model.ModelDefinition.ModelDTO;
import groupId.DTO.Records.Requests.Commands.CreateImageFromFileDTO;
import groupId.DTO.Records.Requests.Commands.ImageConfigDTO;
import groupId.DTO.Records.Requests.Responses.CreateImageResponseDTO;
import groupId.DTO.Records.Requests.Responses.ImagesDTO;
import groupId.Services.ImageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User owned image-related operations.
 */
@RestController
@RequestMapping("user/{userId}/image")
public class UserImageController {

    private final ImageService imageService;
    @Autowired
    public UserImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/model")
    public ResponseEntity<ModelDTO> parseModel(@PathVariable String userId,
                                               @Valid @RequestBody CreateImageFromFileDTO data) {
        ModelDTO response = imageService.parseImage(data.code(),userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateImageResponseDTO> createImage(@PathVariable String userId,
                                                              @Valid @RequestBody ImageDTO image) {
        CreateImageResponseDTO response = imageService.createImage(image,userId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{page}")
    public ResponseEntity<ImagesDTO> getImages(@PathVariable String userId,
                                              @PathVariable int page) {
        ImagesDTO response = imageService.fetchUserImages(page,userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{imageId}")
    public ResponseEntity<Void> configureImage(@PathVariable String userId,
                                               @PathVariable String imageId,
                                               @Valid @RequestBody ImageConfigDTO imgConfig){
        imageService.overrideImage(userId,imageId,imgConfig);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/{imageId}/publish")
    public ResponseEntity<Void> publishImage(@PathVariable String userId,
                                               @PathVariable String imageId){
        imageService.publishImage(userId,imageId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{imageId}/get")
    public ResponseEntity<CreateImageResponseDTO> getPublishedImage(@PathVariable String userId,
                                             @PathVariable String imageId){
        CreateImageResponseDTO response = imageService.addPublishedImage(userId,imageId);
        return ResponseEntity.ok(response);
    }
}
