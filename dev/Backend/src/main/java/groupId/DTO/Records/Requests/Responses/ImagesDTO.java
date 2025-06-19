package groupId.DTO.Records.Requests.Responses;

import groupId.DTO.Records.Image.ImageDTO;

import java.util.Map;
import java.util.UUID;

public record ImagesDTO(Map<UUID,ImageDTO> images, int number, int size, int totalPages,
                        long totalElements, boolean hasNext, boolean hasPrevious) {
}
