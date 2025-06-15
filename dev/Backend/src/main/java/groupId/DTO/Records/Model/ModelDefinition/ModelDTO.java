package groupId.DTO.Records.Model.ModelDefinition;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @param constraints a set of constraints
 * @param preferences a set of preferences
 * @param variables a set of variables
 * @param setTypes a map from a set's name to a list of strings expressing its structure
 * @param paramTypes a map from a parameter's name to a list of strings expressing its structure
 *
 * @see ConstraintDTO
 * @see PreferenceDTO
 * @see VariableDTO
 */
public record ModelDTO(
    Set<ConstraintDTO> constraints,
    Set<PreferenceDTO> preferences,
    Set<VariableDTO> variables,
    Map<String, List<String>> setTypes,
    Map<String, String> paramTypes
    //Map<String,String> varTypes

) {}
