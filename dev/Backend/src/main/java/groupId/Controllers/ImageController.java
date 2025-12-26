package groupId.Controllers;


import groupId.DTO.Records.Requests.Responses.PublishedImagesDTO;
import groupId.Services.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Global image-related operations that do not require user authentication.
 * Not sure is such a thing will exist (browsing published images?)
 */
@RestController
@RequestMapping("image")
@Tag(name = "Standalone Image Operations",
        description = "Actions a user can do with images without authenticating, like viewing existing ones.")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @NonNull
    @Operation(
            summary = "Fetch published images",
            description = "Retrieves a paginated list of published images with optional filtering criteria"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved images"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters provided")
    })
    @GetMapping("/view")
    public ResponseEntity<PublishedImagesDTO> fetchPublishedImages(
            @Parameter(description = "Filter by image name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by image description") @RequestParam(required = false) String description,
            @Parameter(description = "Filter by date before") @RequestParam(required = false) LocalDate before,
            @Parameter(description = "Filter by date after") @RequestParam(required = false) LocalDate after,
            @Parameter(description = "Filter by author name") @RequestParam(required = false) String author,
            @Parameter(description = "Page number (starting from 0, default 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page (default 10)") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(imageService.fetchPublishedImages(page,size,name,description,before,after,author));
    }
}
