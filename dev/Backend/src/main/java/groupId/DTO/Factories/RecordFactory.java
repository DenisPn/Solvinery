package groupId.DTO.Factories;

import Image.Modules.Single.ParameterModule;
import Image.Modules.Single.SetModule;
import Image.Modules.Single.VariableModule;
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
import groupId.DTO.Records.Requests.Responses.ParseModelResponseDTO;
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
    public static SetDTO makeDTO (SetModule set) {

        return new SetDTO(new SetDefinitionDTO(set.getSet().getName(), set.getSet().getDataType().typeList(), set.getAlias()),set.getSet().getData());
    }
    public static SetDefinitionDTO makeDTO (ModelSet set) {

        return new SetDefinitionDTO(set.getName(), set.getDataType().typeList(), null);
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     *//*
    public static Collection<SetDefinitionDTO> makeSetDTOs (Collection<SetModule> sets) {
        LinkedList<SetDefinitionDTO> setDTOs = new LinkedList<>();
        for (SetModule set : sets) {
            setDTOs.add(makeDTO(set));
        }
        return setDTOs;
    }
    public static Collection<SetDefinitionDTO> makeModelSetsDTO (Collection<ModelSet> sets) {
        LinkedList<SetDefinitionDTO> setDTOs = new LinkedList<>();
        for (ModelSet set : sets) {
            setDTOs.add(makeDTO(set));
        }
        return setDTOs;
    }*/

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    public static ParameterDTO makeDTO (ParameterModule parameter) {
        return new ParameterDTO(new ParameterDefinitionDTO(parameter.getParameter().getName(), parameter.getParameter().getDataType().toString(), parameter.getAlias()),parameter.getParameter().getData());
    }
    public static ParameterDefinitionDTO makeDTO (ModelParameter parameter) {
        return new ParameterDefinitionDTO(parameter.getName(), parameter.getDataType().toString(), null);
    }


    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    private static VariableDTO makeDTO (VariableModule variable) {
        return new VariableDTO(variable.getVariable().getName(), variable.getVariable().getStructure(), variable.getAlias());
    }
    /*private static Set<VariableDTO> makeVariableDTOs (Set<VariableModule> vars) {
        Set<VariableDTO> varDTOs = new HashSet<>();
        for (VariableModule var : vars) {
            varDTOs.add(makeDTO(var));
        }
        return varDTOs;
    }*/
    private static VariableDTO makeDTO (Variable variable) {
        return new VariableDTO(variable.getName(), variable.getStructure(), null);
    }

   /* *//**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     *//*
    private static Collection<ParameterDefinitionDTO> makeParamDTOs (Set<ParameterModule> params) {
        LinkedList<ParameterDefinitionDTO> paramDTOs = new LinkedList<>();
        for (ParameterModule param : params) {
            paramDTOs.add(makeDTO(param));
        }
        return paramDTOs;
    }
*/



    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     * Inefficient, maps the whole image, including all its contents into DTOs.
     * should only be called when loading a new Image, not when modifying it.
     */
    public static ImageDTO makeDTO (Image image) {
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



    public static ImageResponseDTO makeDTO (UUID id, Image image) {
        return new ImageResponseDTO(id.toString(), makeDTO(image));
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    @Deprecated
    public static ParseModelResponseDTO makeDTO (UUID id, ModelInterface md) {
        //return new CreateImageResponseDTO(id.toString(), makeDTO(md));
        return null;
    }

    /**
     * DTOs should be created using these methods only.
     * To avoid bloat while reducing coupling between the object and its DTO
     * makeDTO() accepts an internal business object, and converts it to a DTO object-without modifying it.
     */
    public static ModelDTO makeDTO (ModelInterface modelInterface) {
        Set<ConstraintDTO> constraints = new HashSet<>();
        Set<PreferenceDTO> preferences = new HashSet<>();
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
            sets.put(set.getName(),set.getDataType().typeList());
        }
        for (ModelParameter parameter: modelInterface.getParameters()) {
            params.put(parameter.getName(),parameter.getDataType().toString());
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

}
