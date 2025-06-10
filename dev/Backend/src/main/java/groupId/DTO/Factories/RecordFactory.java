package groupId.DTO.Factories;

import Image.Image;
import Image.Modules.Grouping.ConstraintModule;
import Image.Modules.Grouping.PreferenceModule;
import Image.Modules.Single.ParameterModule;
import Image.Modules.Single.SetModule;
import Image.Modules.Single.VariableModule;
import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Elements.Variable;
import Model.ModelInterface;
import Model.Solution;
import groupId.DTO.Records.Image.*;
import groupId.DTO.Records.Model.ModelData.ParameterDTO;
import groupId.DTO.Records.Model.ModelData.ParameterDefinitionDTO;
import groupId.DTO.Records.Model.ModelData.SetDTO;
import groupId.DTO.Records.Model.ModelData.SetDefinitionDTO;
import groupId.DTO.Records.Model.ModelDefinition.*;
import org.springframework.lang.NonNull;

import java.util.*;

/**
 * DTOs should be created using these methods only.
 * To avoid bloat while reducing coupling between the object and its DTO,
 * makeDTO() accepts an internal business object and converts it to a DTO object-without modifying it.
 */
public class RecordFactory {
    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object and converts it to a DTO object-without modifying it.
     */
    @NonNull
    public static SolutionDTO makeDTO (@NonNull Solution solution) {
        if (!solution.isSolved())
            return new SolutionDTO(false, -1, -1, new HashMap<>());
        double solvingTime = solution.getSolvingTime();
        double objectiveValue = solution.getObjectiveValue();
        boolean solved = true;
        HashMap<String, SolutionVariable> variables = new HashMap<>();
        for (String variableName : solution.getActiveVariables()) {
            Set<SolutionValueDTO> variableValues = new HashSet<>();
            List<String> variableStructure = List.copyOf(solution.getVariableStructure(variableName));
            List<String> variableTypes = List.copyOf(solution.getVariableTypes(variableName));
            for (Solution.VariableSolution variableSolution: solution.getVariableSolution(variableName)) {
                variableValues.add(new SolutionValueDTO(variableSolution.solution(), variableSolution.objectiveValue()));
            }
            variables.put(variableName, new SolutionVariable(variableStructure, variableTypes, variableValues));
        }
        return new SolutionDTO(solved, solvingTime, objectiveValue, variables);
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    @NonNull
    public static PreferenceDTO makeDTO (@NonNull Preference preference) {
        return new PreferenceDTO(preference.getName());
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    @NonNull
    public static ConstraintDTO makeDTO (@NonNull Constraint constraint) {
        return new ConstraintDTO(constraint.getName());
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    @NonNull
    public static ConstraintModuleDTO makeDTO (@NonNull ConstraintModule module) {
        /*Set<String> constraints = new HashSet<>();
        for (Constraint constraint : module.getConstraints().values()) {
            constraints.add(constraint.getName());
        }*/
        return new ConstraintModuleDTO(module.getName(), module.getDescription(),
                module.getConstraints(),module.isActive());
    }

    @NonNull
    public static PreferenceModuleDTO makeDTO (@NonNull PreferenceModule module) {
        Set<String> preferences = new HashSet<>();
        for (Preference pref : module.getPreferences().values()) {
            preferences.add(pref.getName());
        }

        return new PreferenceModuleDTO(module.getName(), module.getDescription(),
                preferences,module.getScalar());
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    @NonNull
    public static SetDTO makeDTO (@NonNull SetModule set) {

        return new SetDTO(new SetDefinitionDTO(set.getOriginalName(), set.getOriginalTypes(), set.getName(),set.getTypes())
                ,set.getData());
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    @NonNull
    public static ParameterDTO makeDTO (@NonNull ParameterModule parameter) {
        return new ParameterDTO(new ParameterDefinitionDTO(parameter.getOriginalName(), parameter.getTypeString(), parameter.getName()),
                parameter.getData());
    }
    @NonNull
    public static ParameterDefinitionDTO makeDTO (@NonNull ModelParameter parameter) {
        return new ParameterDefinitionDTO(parameter.getName(), parameter.getDataType().toString(), null);
    }


    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    @NonNull
    private static VariableDTO makeDTO (@NonNull VariableModule variable) {
        return new VariableDTO(variable.getVariable().getName(), variable.getVariable().getTypeStructure(), variable.getAlias());
    }

    @NonNull
    private static VariableDTO makeDTO (@NonNull Variable variable) {
        return new VariableDTO(variable.getName(), variable.getTypeStructure(), null);
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     * Inefficient, maps the whole image, including all its contents into DTOs.
     * should only be called when loading a new Image, not when modifying it.
     */
    @NonNull
    public static ImageDTO makeDTO (@NonNull Image image) {
        Set<VariableDTO> variables=  new HashSet<>();
        Set<ConstraintModuleDTO> constraintModules= new HashSet<>();
        Set<PreferenceModuleDTO> preferenceModules= new HashSet<>();
        Set<SetDTO> sets= new HashSet<>();
        Set<ParameterDTO> parameter=new HashSet<>();
        for(ConstraintModule constraintModule:image.getConstraintsModules().values()){
            constraintModules.add(makeDTO(constraintModule));
        }
        for(PreferenceModule preferenceModule:image.getPreferenceModules().values()){
            preferenceModules.add(makeDTO(preferenceModule));
        }
        for(SetModule setModule:image.getActiveSets()){
            sets.add(makeDTO(setModule));
        }
        for (ParameterModule parameterModule:image.getActiveParams()){
            parameter.add(makeDTO(parameterModule));
        }
        for (VariableModule variableModule:image.getActiveVariables()){
            variables.add(makeDTO(variableModule));
        }
        return new ImageDTO(variables, constraintModules, preferenceModules, sets, parameter,image.getName(),image.getDescription(),image.getSourceCode());
    }


    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    @NonNull
    public static ModelDTO makeDTO (@NonNull ModelInterface modelInterface) {
        Set<ConstraintDTO> constraints = new HashSet<>();
        Set<PreferenceDTO> preferences = new HashSet<>();
        Set<VariableDTO> variables = new HashSet<>();
        Map<String, List<String>> sets = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        for (Constraint constraint : modelInterface.getConstraints()) {
            constraints.add(makeDTO(constraint));
        for (Preference preference : modelInterface.getModifiedPreferences()) {
            preferences.add(makeDTO(preference));
        }
        for (Variable variable : modelInterface.getVariables()) {
            variables.add(makeDTO(variable));
        }
        for (ModelSet set : modelInterface.getSets()) {
            sets.put(set.getName(),set.getDataType().typeList());
        }
        for (ModelParameter parameter: modelInterface.getParameters()) {
            params.put(parameter.getName(),parameter.getDataType().toString());
        }

        }
        return new ModelDTO(constraints, preferences, variables, sets, params);
    }


}
