package DTO.Records.Requests.Responses;

import DTO.Records.Model.ModelDefinition.ModelDTO;

/**
 *
 * @param imageId image ID of the newly created image.
 * @param model model DTO object of the image
 * @see ModelDTO
 */
public record CreateImageResponseDTO (String imageId, ModelDTO model){
    
}
