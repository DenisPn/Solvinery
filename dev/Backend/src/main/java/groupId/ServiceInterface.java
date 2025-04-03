package groupId;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import groupId.DTO.Records.Image.SolutionDTO;
import groupId.DTO.Records.Model.ModelData.InputDTO;
import groupId.DTO.Records.Requests.Commands.CreateImageFromFileDTO;
import groupId.DTO.Records.Requests.Commands.ImageConfigDTO;
import groupId.DTO.Records.Requests.Commands.SolveCommandDTO;
import groupId.DTO.Records.Requests.Responses.CreateImageResponseDTO;

public interface ServiceInterface {
    
    public ResponseEntity<CreateImageResponseDTO> createImage(@RequestBody CreateImageFromFileDTO data) throws Exception;

    public ResponseEntity<Void> configureImage(ImageConfigDTO config) throws Exception;

    public ResponseEntity<SolutionDTO> solve(SolveCommandDTO input) throws Exception;

   // public ResponseEntity<InputDTO> loadImageInput(@PathVariable("id") String imageId) throws Exception ;
}
