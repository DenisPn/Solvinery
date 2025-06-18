package groupId.Controllers;


import groupId.DTO.Records.Requests.Responses.PublishedImagesDTO;
import groupId.Services.ImageService;
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
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @NonNull
    @GetMapping("/view")
    public ResponseEntity<PublishedImagesDTO> fetchPublishedImages(@RequestParam(required = false) String name,
                                                                   @RequestParam(required = false) String description,
                                                                   @RequestParam(required = false) LocalDate before,
                                                                   @RequestParam(required = false) LocalDate after,
                                                                   @RequestParam(required = false) String author,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(imageService.fetchPublishedImages(page,size,name,description,before,after,author));
    }
}
