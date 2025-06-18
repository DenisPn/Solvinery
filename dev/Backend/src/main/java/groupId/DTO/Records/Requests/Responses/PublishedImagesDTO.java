package groupId.DTO.Records.Requests.Responses;

import java.util.Map;
import java.util.UUID;

/**
 * A DTO for a set of published images
 *
 * @param images a set of image data.
 * @param number the page number.
 * @param size the page size.
 * @param totalPages the total number of pages.
 * @param totalElements the total number of elements.
 * @param hasNext if the next page exists.
 * @param hasPrevious if the previous page exists.
 * @see ImageDataDTO
 */
public record PublishedImagesDTO(Map<UUID,ImageDataDTO> images, int number, int size, int totalPages,
                                 long totalElements, boolean hasNext, boolean hasPrevious) {
}
