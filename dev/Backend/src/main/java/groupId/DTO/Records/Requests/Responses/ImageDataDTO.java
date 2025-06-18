package groupId.DTO.Records.Requests.Responses;

import java.time.LocalDate;

/**
 * Data of an image.
 * @param name the name of the image
 * @param description the description of the image
 * @param creationDate the date the image was created
 * @param authorName the image author's name. By default, the user's nickname is used.
 */
public record ImageDataDTO(String name, String description, LocalDate creationDate, String authorName) {
}
