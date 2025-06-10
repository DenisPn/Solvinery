package groupId.Controllers;


import groupId.DTO.Records.Requests.Responses.PublishedImagesDTO;
import groupId.Services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/view/{page}")
    public ResponseEntity<PublishedImagesDTO> fetchPublishedImages(@PathVariable int page) {
        return ResponseEntity.ok(imageService.fetchPublishedImages(page));
    }
}
