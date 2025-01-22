package groupId;

import DTO.Records.Requests.Responses.ImageResponseDTO;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import DTO.Records.Requests.Commands.CreateImageFromFileDTO;
import DTO.Records.Requests.Commands.CreateImageFromPathDTO;
import DTO.Records.Requests.Commands.ImageConfigDTO;
import DTO.Records.Requests.Commands.SolveCommandDTO;
import DTO.Records.Image.SolutionDTO;

public interface ServiceInterface {
    
    public ResponseEntity<ImageResponseDTO> createImage(@RequestBody CreateImageFromFileDTO data) throws IOException;

    public ResponseEntity<Void> configureImage(ImageConfigDTO config) throws Exception;

    public ResponseEntity<SolutionDTO> solve(SolveCommandDTO input) throws Exception;
}
