package groupId.DTO.Records.Requests.Commands;

import groupId.DTO.Records.Image.ImageDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * This record encapsulates the identifier of the image and the image
 * @param userUUID UUID of the image owner.
 * @param imageId UUID of the image.
 * @param image {@link ImageDTO} object containing the details of the image.
 */
public record ImageConfigDTO(
        @NotBlank String userUUID,
        @NotBlank String imageId,
        @Valid @NotNull ImageDTO image) { }
