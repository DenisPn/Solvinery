package groupId.DTO.Factories;

import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Elements.Variable;
import groupId.DTO.Records.Image.*;
import groupId.DTO.Records.Model.ModelDefinition.*;
import groupId.DTO.Records.Model.ModelData.ParameterDTO;
import groupId.DTO.Records.Model.ModelData.ParameterDefinitionDTO;
import groupId.DTO.Records.Model.ModelData.SetDTO;
import groupId.DTO.Records.Model.ModelData.SetDefinitionDTO;
import groupId.DTO.Records.Requests.Commands.CreateImageFromFileDTO;
import groupId.DTO.Records.Requests.Responses.CreateImageResponseDTO;
import groupId.DTO.Records.Requests.Responses.ImageResponseDTO;
import Image.Image;
import Image.Modules.Grouping.ConstraintModule;
import Image.Modules.Grouping.PreferenceModule;
import Model.*;
import org.yaml.snakeyaml.util.Tuple;

import java.util.*;

/**
 * DTOs should be created using these methods only.
 * To avoid bloat while reducing coupling between the object and its DTO
 * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
 */
public class RecordFactory {
    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    public static SolutionDTO makeDTO (Solution solution) {
        Objects.requireNonNull(solution, "Null Solution in DTO map");
        if (!solution.parsed())
            throw new RuntimeException("Solution must be parsed before attempting to convert to DTO.");
        if (!solution.isSolved())
            return new SolutionDTO(false, -1, -1, "", new HashMap<>());
        double solvingTime = solution.getSolvingTime();
        double objectiveValue = solution.getObjectiveValue();
        boolean solved = true;
        HashMap<String, SolutionVariable> variables = new HashMap<>();
        for (Variable variable : solution.getVariables()) {
            String variableName = variable.getName();
            Set<SolutionValueDTO> variableValues = new HashSet<>();
            List<String> variableStructure = List.copyOf(solution.getVariableStructure(variableName));
            List<String> variableTypes = List.copyOf(solution.getVariableTypes(variableName));
            for (Tuple<List<String>, Integer> value : solution.getVariableSolution(variableName)) {
                variableValues.add(new SolutionValueDTO(value._1(), value._2()));
            }
            variables.put(variableName, new SolutionVariable(variableStructure, variableTypes, variableValues));
        }
        return new SolutionDTO(solved, solvingTime, objectiveValue, "", variables);
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    public static PreferenceDTO makeDTO (Preference preference) {
        if (preference == null)
            throw new NullPointerException("Null preference in DTO mapping");
        HashSet<ModelSet> sets = new HashSet<>();
        //preference.getPrimitiveSets(sets);
        HashSet<ModelParameter> parameters = new HashSet<>();
        //preference.getPrimitiveParameters(parameters);
        return new PreferenceDTO(preference.getName()/*, makeDTO(sets, parameters)*/);
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    public static ConstraintDTO makeDTO (Constraint constraint) {
        if (constraint == null)
            throw new NullPointerException("Null constraint in DTO mapping");
        HashSet<ModelSet> sets = new HashSet<>();
        //constraint.getPrimitiveSets(sets);
        HashSet<ModelParameter> parameters = new HashSet<>();
        //constraint.getPrimitiveParameters(parameters);
        return new ConstraintDTO(constraint.getName()/*, makeDTO(sets, parameters)*/);
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    public static ConstraintModuleDTO makeDTO (ConstraintModule module) {
        if (module == null)
            throw new NullPointerException("Null constraint module in DTO mapping");
        Set<String> constraints = new HashSet<>();
        for (Constraint constraint : module.getConstraints().values()) {
            constraints.add(constraint.getName());
        }
        return new ConstraintModuleDTO(module.getName(), module.getDescription(),
                constraints);
    }

    public static PreferenceModuleDTO makeDTO (PreferenceModule module) {
        if (module == null)
            throw new NullPointerException("Null preference module in DTO mapping");
        Set<String> preferences = new HashSet<>();
        for (Preference pref : module.getPreferences().values()) {
            preferences.add(pref.getName());
        }

        return new PreferenceModuleDTO(module.getName(), module.getDescription(),
                preferences);
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    public static SetDefinitionDTO makeDTO (ModelSet set) {
        LinkedList<String> dependencies = new LinkedList<>();
        /*for (ModelSet dependency : set.getSetDependencies()) {
            for (ModelInput.StructureBlock block : dependency.getStructure()) {
                dependencies.add(block.dependency.getName());
            }
        }*/
        return new SetDefinitionDTO(set.getName(), dependencies, set.getType().typeList());
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    public static Collection<SetDefinitionDTO> makeDTO (Collection<ModelSet> sets) {
        LinkedList<SetDefinitionDTO> setDTOs = new LinkedList<>();
        for (ModelSet set : sets) {
            setDTOs.add(makeDTO(set));
        }
        return setDTOs;
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    public static ParameterDefinitionDTO makeDTO (ModelParameter parameter) {
        return new ParameterDefinitionDTO(parameter.getName(), parameter.getType().toString());
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    public static ParameterDTO makeDTO (ModelParameter parameter, String value) {
        return new ParameterDTO(makeDTO(parameter), value);
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    private static VariableDTO makeDTO (Variable variable) {
        HashSet<ModelSet> sets = new HashSet<>();
       // variable.getPrimitiveSets(sets);
        HashSet<ModelParameter> parameters = new HashSet<>();
       // variable.getPrimitiveParameters(parameters);
        return new VariableDTO(variable.getName(), variable.getStructure()/*, makeDTO(sets, parameters)*/);
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    private static Collection<ParameterDefinitionDTO> makeDTO (Set<ModelParameter> params) {
        LinkedList<ParameterDefinitionDTO> paramDTOs = new LinkedList<>();
        for (ModelParameter param : params) {
            paramDTOs.add(makeDTO(param));
        }
        return paramDTOs;
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    public static SetDTO makeDTO (ModelSet set, List<String> values) {
        return new SetDTO(makeDTO(set), values);
    }


    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     * Inefficient, maps the whole image, including all its contents into DTOs.
     * should only be called when loading a new Image, not when modifying it.
     */
    public static ImageDTO makeDTO (Image image) {
        //TODO: image under refactoring, implement when its finished.
        /*if (image == null)
            throw new NullPointerException("Null image in DTO mapping");
        Set<ConstraintModuleDTO> constraints = new HashSet<>();
        Set<PreferenceModuleDTO> preferences = new HashSet<>();
        VariableModuleDTO variables = makeDTO(image.getActiveVariables().stream().toList(), image.getAliases());
        for (ConstraintModule module : image.getConstraintsModules().values()) {
            constraints.add(makeDTO(module));
        }
        for (PreferenceModule module : image.getPreferenceModules().values()) {
            preferences.add(makeDTO(module));
        }

        return new ImageDTO(variables, constraints, preferences);*/
        return new ImageDTO(null,null,null);
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    private static VariableModuleDTO makeDTO (List<Variable> values, Map<String, String> aliases) {
        Set<String> variables = new HashSet<>();
        Set<String> params = new HashSet<>();
        Set<String> sets = new HashSet<>();
        for (Variable mv : values) {
            variables.add(mv.getName());
            /*for (ModelSet set : mv.getSetDependencies()) {
                sets.add(set.getName());
            }
            for (ModelParameter param : mv.getParamDependencies()) {
                params.add(param.getName());
            }*/
        }
        return new VariableModuleDTO(variables/*, sets, params*/, aliases);
    }

    public static ImageResponseDTO makeDTO (UUID id, Image image) {
        return new ImageResponseDTO(id.toString(), makeDTO(image));
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    public static CreateImageResponseDTO makeDTO (UUID id, ModelInterface md) {
        return new CreateImageResponseDTO(id.toString(), makeDTO(md));
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    private static ModelDTO makeDTO (ModelInterface modelInterface) {
        Set<ConstraintDTO> constraints = new HashSet<>();
        Set<PreferenceDTO> preferences = new HashSet<>();
        //TODO: Variables.
        Set<VariableDTO> variables = new HashSet<>();
        Map<String, List<String>> sets = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        Map<String, String> varTypes = new HashMap<>();
        for (Constraint constraint : modelInterface.getConstraints()) {
            constraints.add(makeDTO(constraint));
        for (Preference preference : modelInterface.getPreferences()) {
            preferences.add(makeDTO(preference));
        }
        for (Variable variable : modelInterface.getVariables()) {
            variables.add(makeDTO(variable));
        }
        for (ModelSet set : modelInterface.getSets()) {
            sets.put(set.getName(),set.getType().typeList());
        }
        for (ModelParameter parameter: modelInterface.getParameters()) {
            params.put(parameter.getName(),parameter.getType().toString());
        }

        }
        return new ModelDTO(constraints, preferences, variables, sets, params);
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    @Deprecated
    public static DependenciesDTO makeDTO (Set<ModelSet> sets, Set<ModelParameter> parameters) {
       /* Set<String> resS = new HashSet<>();
        Set<String> resP = new HashSet<>();
        for (ModelSet x : sets) {
            resS.add(x.getName());
        }
        for (ModelParameter x : parameters) {
            resP.add(x.getName());
        }
        //return new DependenciesDTO(resS, resP);*/
        return new DependenciesDTO();
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    public static CreateImageFromFileDTO makeDTO (String code) {
        return new CreateImageFromFileDTO(code);
    }

}
