package groupId.DTO.Records.Requests.Responses;

import java.util.Set;

/**
 * A DTO for a set of published images
 * @param images a set of image data.
 * @see ImageDataDTO
 */
public record PublishedImagesDTO(Set<ImageDataDTO> images) {
}
