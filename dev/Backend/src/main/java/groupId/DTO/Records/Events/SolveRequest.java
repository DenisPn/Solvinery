package groupId.DTO.Records.Events;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
@Data
@AllArgsConstructor
@NoArgsConstructor  // This is important for serialization!
public class SolveRequest {
    private String userId;
    private String problemId;
    private String zimplContent;
}
*/

public record SolveRequest(
        String requestId,
        String zimplContent,
        int timeoutSeconds)
{ }

