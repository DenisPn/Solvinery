package groupId.DTO.Records.Requests.Responses;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A DTO for a set of published images
 * @param images a set of image data.
 * @see ImageDataDTO
 */
public record PublishedImagesDTO(Map<UUID,ImageDataDTO> images) {
}
