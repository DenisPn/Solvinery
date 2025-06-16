package groupId.Controllers;

import groupId.DTO.Records.Image.ImageDTO;
import groupId.DTO.Records.Image.SolutionDTO;
import groupId.DTO.Records.Model.ModelDefinition.ModelDTO;
import groupId.DTO.Records.Requests.Commands.CreateImageFromFileDTO;
import groupId.DTO.Records.Requests.Commands.ImageConfigDTO;
import groupId.DTO.Records.Requests.Responses.CreateImageResponseDTO;
import groupId.DTO.Records.Requests.Responses.ImagesDTO;
import groupId.Services.ImageService;
import groupId.Services.SolveService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

/**
 * User owned image-related operations.
 */
@RestController
@RequestMapping("user/{userId}/image")
public class UserImageController {

    private final ImageService imageService;

    private final SolveService solveService;
    @Autowired
    public UserImageController(ImageService imageService,SolveService solveService) {
        this.imageService = imageService;
        this.solveService = solveService;
    }

    @NonNull
    @PostMapping("/model")
    public ResponseEntity<ModelDTO> parseModel(@NonNull @PathVariable String userId,
                                               @NonNull @Valid @RequestBody CreateImageFromFileDTO data) {
        solveService.validateThreaded(data.code());
        ModelDTO response = imageService.parseImage(data.code(),userId);
        return ResponseEntity.ok(response);
    }

    @NonNull
    @PostMapping
    public ResponseEntity<CreateImageResponseDTO> createImage(@NonNull @PathVariable String userId,
                                                              @NonNull @Valid @RequestBody ImageDTO image) {
        solveService.validateThreaded(image.code());
        CreateImageResponseDTO response = imageService.createImage(image,userId);
        return ResponseEntity.ok(response);
    }
    @NonNull
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@NonNull @PathVariable String userId,
                                                              @NonNull @PathVariable String imageId) {
        imageService.deleteImage(userId,imageId);
        return ResponseEntity.ok().build();
    }
    @NonNull
    @GetMapping("/{page}")
    public ResponseEntity<ImagesDTO> getImages(@NonNull @PathVariable String userId,
                                               @PathVariable int page) {
        ImagesDTO response = imageService.fetchUserImages(page,userId);
        return ResponseEntity.ok(response);
    }

    @NonNull
    @PatchMapping("/{imageId}")
    public ResponseEntity<Void> configureImage(@NonNull @PathVariable String userId,
                                               @NonNull @PathVariable String imageId,
                                               @NonNull @Valid @RequestBody ImageDTO imageDTO){
        imageService.overrideImage(userId,imageId,imageDTO);
        return ResponseEntity.ok().build();
    }
    @NonNull
    @PatchMapping("/{imageId}/publish")
    public ResponseEntity<Void> publishImage(@NonNull @PathVariable String userId,
                                             @NonNull @PathVariable String imageId){
        imageService.publishImage(userId,imageId);
        return ResponseEntity.ok().build();
    }

    @NonNull
    @PatchMapping("/{imageId}/get")
    public ResponseEntity<CreateImageResponseDTO> getPublishedImage(@NonNull @PathVariable String userId,
                                                                    @NonNull @PathVariable String imageId){
        CreateImageResponseDTO response = imageService.addPublishedImage(userId,imageId);
        return ResponseEntity.ok(response);
    }
    @NonNull
    @PostMapping("/{imageId}/solver")
    public ResponseEntity<SolutionDTO> solve(@NonNull @PathVariable String userId,
                                             @NonNull @PathVariable String imageId,
                                             @NonNull @Valid @RequestBody ImageConfigDTO config){
        SolutionDTO response = solveService.solveThreaded(userId,imageId, config);
        return ResponseEntity.ok(response);
    }
}
