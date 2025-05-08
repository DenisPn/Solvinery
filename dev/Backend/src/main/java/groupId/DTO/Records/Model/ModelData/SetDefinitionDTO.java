package groupId.DTO.Records.Model.ModelData;

import java.util.List;

/**
 *
 * @param name The set's name

 * @param type the types the set is made of, size one if its normal set, or the types of its composition if it's complex
 */
public record SetDefinitionDTO(String name, List<String> type, String alias) {
}
