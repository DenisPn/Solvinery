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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * User owned image-related operations.
 */
@RestController
@RequestMapping("user/{userId}/image")
@Tag(name = "User image operations",
        description = "Image actions available only to authorized users, depending on image permissions.")
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
    @Operation(summary = "Parse image model", description = "Parses the model definition from the provided image file")
    public ResponseEntity<ModelDTO> parseModel(@NonNull @PathVariable String userId,
                                               @NonNull @Valid @RequestBody CreateImageFromFileDTO data) {
        solveService.validateThreaded(data.code());
        ModelDTO response = imageService.parseImage(data.code(),userId);
        return ResponseEntity.ok(response);
    }

    @NonNull
    @PostMapping
    @Operation(summary = "Create new image", description = "Creates a new image from the provided image data")
    public ResponseEntity<CreateImageResponseDTO> createImage(@NonNull @PathVariable String userId,
                                                              @NonNull @Valid @RequestBody ImageDTO image) {
        solveService.validateThreaded(image.code());
        CreateImageResponseDTO response = imageService.createImage(image,userId);
        return ResponseEntity.ok(response);
    }
    @NonNull
    @DeleteMapping("/{imageId}")
    @Operation(summary = "Delete image", description = "Deletes the specified image")
    public ResponseEntity<Void> deleteImage(@NonNull @PathVariable String userId,
                                                              @NonNull @PathVariable String imageId) {
        imageService.deleteImage(userId,imageId);
        return ResponseEntity.ok().build();
    }
    @NonNull
    @GetMapping("/view")
    @Operation(summary = "Get user images", description = "Retrieves a paginated list of images owned by the user")
    public ResponseEntity<ImagesDTO> getImages(@NonNull @PathVariable String userId,
                                               @RequestParam(required = false) String name,
                                               @RequestParam(required = false) String description,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        ImagesDTO response = imageService.fetchUserImages(page,size,userId,name,description);
        return ResponseEntity.ok(response);
    }

    @NonNull
    @PatchMapping("/{imageId}")
    @Operation(summary = "Configure image", description = "Updates the configuration of an existing image")
    public ResponseEntity<Void> configureImage(@NonNull @PathVariable String userId,
                                               @NonNull @PathVariable String imageId,
                                               @NonNull @Valid @RequestBody ImageDTO imageDTO,
                                               @RequestParam(defaultValue = "false") boolean ignoreData){
        imageService.overrideImage(userId,imageId,imageDTO,ignoreData,solveService);
        return ResponseEntity.ok().build();
    }
    @NonNull
    @PatchMapping("/{imageId}/publish")
    @Operation(summary = "Publish image", description = "Makes the image available for other users")
    public ResponseEntity<Void> publishImage(@NonNull @PathVariable String userId,
                                             @NonNull @PathVariable String imageId){
        imageService.publishImage(userId,imageId);
        return ResponseEntity.ok().build();
    }

    @NonNull
    @PatchMapping("/{imageId}/get")
    @Operation(summary = "Get published image", description = "Adds a published image to user's collection")
    public ResponseEntity<CreateImageResponseDTO> getPublishedImage(@NonNull @PathVariable String userId,
                                                                    @NonNull @PathVariable String imageId){
        CreateImageResponseDTO response = imageService.addPublishedImage(userId,imageId);
        return ResponseEntity.ok(response);
    }
    @NonNull
    @PostMapping("/{imageId}/solver")
    @Operation(summary = "Solve image", description = "Attempts to solve the image with given configuration")
    public ResponseEntity<SolutionDTO> solve(@NonNull @PathVariable String userId,
                                             @NonNull @PathVariable String imageId,
                                             @NonNull @Valid @RequestBody ImageConfigDTO config){
        SolutionDTO response = solveService.solveThreaded(userId,imageId, config);
        return ResponseEntity.ok(response);
    }
}
