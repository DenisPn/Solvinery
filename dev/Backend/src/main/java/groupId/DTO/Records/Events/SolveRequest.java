package groupId.DTO.Records.Events;



public record SolveRequest(
        String requestId,
        String zimplContent,
        int timeoutSeconds,
        boolean validationOnly
        )
{ }

