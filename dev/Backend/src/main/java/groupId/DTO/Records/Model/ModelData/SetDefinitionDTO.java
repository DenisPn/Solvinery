package groupId.DTO.Records.Model.ModelData;

import java.util.List;

/**
 *
 * @param name The set's name
 * @param composition The set's composition, if it is comprised from an operation on other sets
 *                    (e.g. if it is a product of two sets, the list will include their names-in the correct order)
 * @param type the types the set is made of, size one if its a normal set, or the types of its composition if its complex
 */
public record SetDefinitionDTO(String name, List<String> composition, List<String> type, String alias) {
}
