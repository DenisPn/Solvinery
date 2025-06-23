package Utilities.Builders;

import groupId.DTO.Records.Image.ConstraintModuleDTO;
import groupId.DTO.Records.Image.ImageDTO;
import groupId.DTO.Records.Image.PreferenceModuleDTO;
import groupId.DTO.Records.Model.ModelData.ParameterDTO;
import groupId.DTO.Records.Model.ModelData.SetDTO;
import groupId.DTO.Records.Model.ModelData.SetDefinitionDTO;
import groupId.DTO.Records.Model.ModelDefinition.VariableDTO;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class ImageDTOBuilder {
    @NonNull
    private Map<String,VariableDTO> variables = new HashMap<>();
    @NonNull
    private Map<String,ConstraintModuleDTO> constraintModules = new HashMap<>();
    @NonNull
    private Map<String,PreferenceModuleDTO> preferenceModules = new HashMap<>();
    @NonNull
    private Map<String,SetDTO> sets = new HashMap<>();
    @NonNull
    private Map<String,ParameterDTO> parameters = new HashMap<>();
    private String name;
    private String description;
    private String code;
    private Boolean ignoreData;
    public ImageDTOBuilder() {}
    public ImageDTOBuilder(@NonNull ImageDTO imageDTO) {
        this.variables = imageDTO.variables().stream().collect(Collectors.toMap(VariableDTO::identifier, variableDTO -> variableDTO));
        this.constraintModules = imageDTO.constraintModules().stream().collect(Collectors.toMap(ConstraintModuleDTO::moduleName, constraintModuleDTO -> constraintModuleDTO));
        this.preferenceModules = imageDTO.preferenceModules().stream().collect(Collectors.toMap(PreferenceModuleDTO::moduleName, preferenceModuleDTO -> preferenceModuleDTO));
        this.sets = imageDTO.sets().stream().collect(Collectors.toMap(setDTO -> setDTO.setDefinition().name(), setDTO -> setDTO));
        this.parameters = imageDTO.parameters().stream().collect(Collectors.toMap(parameterDTO -> parameterDTO.parameterDefinition().name(), parameterDTO -> parameterDTO));
        this.name = imageDTO.name();
        this.description = imageDTO.description();
        this.code = imageDTO.code();
    }
    @NonNull
    public ImageDTOBuilder ignoreData() {
        this.ignoreData = true;
        return this;
    }
    @NonNull
    public ImageDTOBuilder dontIgnoreData() {
        this.ignoreData = false;
        return this;
    }
    @NonNull
    public ImageDTOBuilder withName(String name) {
        this.name = name;
        return this;
    }

    @NonNull
    public ImageDTOBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
    @NonNull
    public ImageDTOBuilder withCode(String code) {
        this.code = code;
        return this;
    }
    @NonNull
    public ImageDTOBuilder withVariables(@NonNull Set<VariableDTO> variables) {
        variables.forEach(variableDTO -> this.variables.put(variableDTO.identifier(), variableDTO));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withVariables(@NonNull VariableDTO... variables) {
        this.variables.putAll(Arrays.stream(variables).collect(Collectors.toMap(VariableDTO::identifier, variableDTO -> variableDTO)));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withVariableName(String currentName, String newName) {
        if(!this.variables.containsKey(currentName))
            throw new IllegalArgumentException("Builder error: variable with name: " + currentName + " doesn't exist");
        variables.computeIfPresent(currentName, (k, variableDTO) -> new VariableDTO(newName, variableDTO.structure(), variableDTO.alias(),variableDTO.objectiveValueAlias()));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withVariableAlias(String variableName, String alias) {
        if(!this.variables.containsKey(variableName))
            throw new IllegalArgumentException("Builder error: variable with name: " + variableName + " doesn't exist");
        variables.computeIfPresent(variableName, (k, variableDTO) -> new VariableDTO(variableDTO.identifier(), variableDTO.structure(), alias,variableDTO.objectiveValueAlias()));
    return this;
    }
    public ImageDTOBuilder withVariableObjectiveValueAlias(String variableName, String objectiveValueAlias) {
        if(!this.variables.containsKey(variableName))
            throw new IllegalArgumentException("Builder error: variable with name: " + variableName + " doesn't exist");
        variables.computeIfPresent(variableName, (k, variableDTO) -> new VariableDTO(variableDTO.identifier(), variableDTO.structure(), variableDTO.alias(), objectiveValueAlias));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withVariableStructure(String variableName, List<String> structure) {
        if(!this.variables.containsKey(variableName))
            throw new IllegalArgumentException("Builder error: variable with name: " + variableName + " doesn't exist");
        variables.computeIfPresent(variableName, (k, variableDTO) -> new VariableDTO(variableDTO.identifier(), structure, variableDTO.alias(),variableDTO.objectiveValueAlias()));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withoutVariables(String... variables) {
        Arrays.asList(variables).forEach(name -> this.variables.remove(name));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withConstraintModules(@NonNull Set<ConstraintModuleDTO> constraintModules) {
        constraintModules.forEach(constraintModuleDTO -> this.constraintModules.put(constraintModuleDTO.moduleName(), constraintModuleDTO));
        return this;
    }

    @NonNull
    public ImageDTOBuilder withConstraintModules(@NonNull ConstraintModuleDTO... constraintModules) {
        this.constraintModules.putAll(Arrays.stream(constraintModules).collect(Collectors.toMap(ConstraintModuleDTO::moduleName, constraintModuleDTO -> constraintModuleDTO)));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withConstraintModuleName(String currentName, String newName) {
        if(!this.constraintModules.containsKey(currentName))
            throw new IllegalArgumentException("Builder error: constraint module with name: " + currentName + " doesn't exist");
        constraintModules.computeIfPresent(currentName, (k, constraintModuleDTO) -> new ConstraintModuleDTO(newName, constraintModuleDTO.description(), constraintModuleDTO.constraints(), constraintModuleDTO.active()));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withConstraintModuleConstraints(String constraintModuleName, Set<String> constraints) {
        if(!this.constraintModules.containsKey(constraintModuleName))
            throw new IllegalArgumentException("Builder error: constraint module with name: " + constraintModuleName + " doesn't exist");
        constraintModules.computeIfPresent(constraintModuleName, (k, constraintModuleDTO) -> new ConstraintModuleDTO(constraintModuleDTO.moduleName(), constraintModuleDTO.description(), constraints, constraintModuleDTO.active()));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withConstraintModuleDescription(String constraintModuleName, String description) {
        if(!this.constraintModules.containsKey(constraintModuleName))
            throw new IllegalArgumentException("Builder error: constraint module with name: " + constraintModuleName + " doesn't exist");
        constraintModules.computeIfPresent(constraintModuleName, (k, constraintModuleDTO) -> new ConstraintModuleDTO(constraintModuleDTO.moduleName(), description, constraintModuleDTO.constraints(), constraintModuleDTO.active()));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withoutConstraintModules(String... constraintModules) {
        Arrays.asList(constraintModules).forEach(name -> this.constraintModules.remove(name));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withPreferenceModules(@NonNull Set<PreferenceModuleDTO> preferenceModules) {
        preferenceModules.forEach(preferenceModuleDTO -> this.preferenceModules.put(preferenceModuleDTO.moduleName(), preferenceModuleDTO));
        return this;
    }

    @NonNull
    public ImageDTOBuilder withPreferenceModules(@NonNull PreferenceModuleDTO... preferenceModules) {
        this.preferenceModules.putAll(Arrays.stream(preferenceModules).collect(Collectors.toMap(PreferenceModuleDTO::moduleName, preferenceModuleDTO -> preferenceModuleDTO)));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withPreferenceModuleName(String currentName, String newName) {
        if(!this.preferenceModules.containsKey(currentName))
            throw new IllegalArgumentException("Builder error: preference module with name: " + currentName + " doesn't exist");
        preferenceModules.computeIfPresent(currentName, (k, preferenceModuleDTO) -> new PreferenceModuleDTO(newName, preferenceModuleDTO.description(), preferenceModuleDTO.preferences(), preferenceModuleDTO.scalar()));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withPreferenceModulePreferences(String preferenceModuleName, Set<String> preferences) {
        if(!this.preferenceModules.containsKey(preferenceModuleName))
            throw new IllegalArgumentException("Builder error: preference module with name: " + preferenceModuleName + " doesn't exist");
        preferenceModules.computeIfPresent(preferenceModuleName, (k, preferenceModuleDTO) -> new PreferenceModuleDTO(preferenceModuleDTO.moduleName(), preferenceModuleDTO.description(), preferences, preferenceModuleDTO.scalar()));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withPreferenceModuleDescription(String preferenceModuleName, String description) {
        if(!this.preferenceModules.containsKey(preferenceModuleName))
            throw new IllegalArgumentException("Builder error: preference module with name: " + preferenceModuleName + " doesn't exist");
        preferenceModules.computeIfPresent(preferenceModuleName, (k, preferenceModuleDTO) -> new PreferenceModuleDTO(preferenceModuleDTO.moduleName(), description, preferenceModuleDTO.preferences(), preferenceModuleDTO.scalar()));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withPreferenceModuleScalar(String preferenceModuleName, Float scalar) {
        if(!this.preferenceModules.containsKey(preferenceModuleName))
            throw new IllegalArgumentException("Builder error: preference module with name: " + preferenceModuleName + " doesn't exist");
        preferenceModules.computeIfPresent(preferenceModuleName, (k, preferenceModuleDTO) -> new PreferenceModuleDTO(preferenceModuleDTO.moduleName(), preferenceModuleDTO.description(), preferenceModuleDTO.preferences(), scalar));
        return this;
    }

    @NonNull
    public ImageDTOBuilder withoutPreferenceModules(String... preferenceModules) {
        Arrays.asList(preferenceModules).forEach(name -> this.preferenceModules.remove(name));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withSets(@NonNull Set<SetDTO> sets) {
        sets.forEach(setDTO -> this.sets.put(setDTO.setDefinition().name(), setDTO));
        return this;
    }

    @NonNull
    public ImageDTOBuilder withSets(@NonNull SetDTO... sets) {
        this.sets.putAll(Arrays.stream(sets).collect(Collectors.toMap(setDTO -> setDTO.setDefinition().name(), setDTO -> setDTO)));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withSetName(String currentName, String newName) {
        if(!this.sets.containsKey(currentName))
            throw new IllegalArgumentException("Builder error: set with name: " + currentName + " doesn't exist");
        sets.computeIfPresent(currentName, (k, setDTO) -> new SetDTO(new SetDefinitionDTO(newName,setDTO.setDefinition().structure(),setDTO.setDefinition().alias()), setDTO.values()));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withSetValues(String setName, List<String> values) {
        if(!this.sets.containsKey(setName))
            throw new IllegalArgumentException("Builder error: set with name: " + setName + " doesn't exist");
        sets.computeIfPresent(setName, (k, setDTO) -> new SetDTO(setDTO.setDefinition(), values));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withSetStructureAlias(String setName, List<String> structure){
        if(!this.sets.containsKey(setName))
            throw new IllegalArgumentException("Builder error: set with name: " + setName + " doesn't exist");
        sets.computeIfPresent(setName, (k, setDTO) -> new SetDTO(new SetDefinitionDTO(setDTO.setDefinition().name(),structure,setDTO.setDefinition().alias()), setDTO.values()));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withSetAlias(String setName, String alias){
        if(!this.sets.containsKey(setName))
            throw new IllegalArgumentException("Builder error: set with name: " + setName + " doesn't exist");
        sets.computeIfPresent(setName, (k, setDTO) -> new SetDTO(new SetDefinitionDTO(setDTO.setDefinition().name(),setDTO.setDefinition().structure(),alias), setDTO.values()));
        return this;
    }

    @NonNull
    public ImageDTOBuilder withoutSets(String... sets) {
        Arrays.asList(sets).forEach(setName -> this.sets.remove(setName));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withParameters(@NonNull Set<ParameterDTO> parameters) {
        parameters.forEach(parameterDTO -> this.parameters.put(parameterDTO.parameterDefinition().name(), parameterDTO));
        return this;
    }

    @NonNull
    public ImageDTOBuilder withParameters(@NonNull ParameterDTO... parameters) {
        this.parameters.putAll(Arrays.stream(parameters).collect(Collectors.toMap(parameterDTO -> parameterDTO.parameterDefinition().name(), parameterDTO -> parameterDTO)));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withParameterName(String currentName, String newName) {
        if(!this.parameters.containsKey(currentName))
            throw new IllegalArgumentException("Builder error: parameter with name: " + currentName + " doesn't exist");
        parameters.computeIfPresent(currentName, (k, parameterDTO) -> new ParameterDTO(new groupId.DTO.Records.Model.ModelData.ParameterDefinitionDTO(newName,parameterDTO.parameterDefinition().structure(),parameterDTO.parameterDefinition().alias()), parameterDTO.value()));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withParameterValue(String parameterName, String value) {
        if(!this.parameters.containsKey(parameterName))
            throw new IllegalArgumentException("Builder error: parameter with name: " + parameterName + " doesn't exist");
        parameters.computeIfPresent(parameterName, (k, parameterDTO) -> new ParameterDTO(parameterDTO.parameterDefinition(), value));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withParameterStructureAlias(String parameterName, String structure){
        if(!this.parameters.containsKey(parameterName))
            throw new IllegalArgumentException("Builder error: parameter with name: " + parameterName + " doesn't exist");
        parameters.computeIfPresent(parameterName, (k, parameterDTO) -> new ParameterDTO(new groupId.DTO.Records.Model.ModelData.ParameterDefinitionDTO(parameterDTO.parameterDefinition().name(),structure,parameterDTO.parameterDefinition().alias()), parameterDTO.value()));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withParameterAlias(String parameterName, String alias){
        if(!this.parameters.containsKey(parameterName))
            throw new IllegalArgumentException("Builder error: parameter with name: " + parameterName + " doesn't exist");
        parameters.computeIfPresent(parameterName, (k, parameterDTO) -> new ParameterDTO(new groupId.DTO.Records.Model.ModelData.ParameterDefinitionDTO(parameterDTO.parameterDefinition().name(),parameterDTO.parameterDefinition().structure(),alias), parameterDTO.value()));
        return this;
    }
    @NonNull
    public ImageDTOBuilder withoutParameters(String... parameters) {
        Arrays.asList(parameters).forEach(name -> this.parameters.remove(name));
        return this;
    }
    @NonNull
    public ImageDTOBuilder copy() {
        ImageDTOBuilder clone= new ImageDTOBuilder();
        clone.variables = new HashMap<>(this.variables);
        clone.constraintModules = new HashMap<>(this.constraintModules);
        clone.preferenceModules = new HashMap<>(this.preferenceModules);
        clone.sets = new HashMap<>(this.sets);
        clone.parameters = new HashMap<>(this.parameters);
        clone.name = this.name;
        clone.description = this.description;
        clone.code = this.code;
        return clone;
    }
    public ImageDTOBuilder clear(){
        variables.clear();
        constraintModules.clear();
        preferenceModules.clear();
        sets.clear();
        parameters.clear();
        name = null;
        description = null;
        code = null;
        return this;
    }
    @NonNull
    public ImageDTO build() {
        return new ImageDTO(Set.copyOf(variables.values()),
                Set.copyOf(constraintModules.values()),
                Set.copyOf(preferenceModules.values()),
                Set.copyOf(sets.values()),
                Set.copyOf(parameters.values()),
                name,
                description,
                code
        );
    }

}
