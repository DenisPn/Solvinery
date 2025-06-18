package Image;

import Exceptions.InternalErrors.ClientSideError;
import Exceptions.InternalErrors.ModelExceptions.InvalidModelStateException;
import Exceptions.UserErrors.UserInputException;
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
import Model.Model;
import Model.ModelInterface;
import Model.ModelProxy;
import groupId.DTO.Records.Image.ConstraintModuleDTO;
import groupId.DTO.Records.Image.ImageDTO;
import groupId.DTO.Records.Image.PreferenceModuleDTO;
import groupId.DTO.Records.Model.ModelData.ParameterDTO;
import groupId.DTO.Records.Model.ModelData.SetDTO;
import groupId.DTO.Records.Model.ModelDefinition.ConstraintDTO;
import groupId.DTO.Records.Model.ModelDefinition.PreferenceDTO;
import groupId.DTO.Records.Model.ModelDefinition.VariableDTO;
import groupId.DTO.Records.Requests.Commands.ImageConfigDTO;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Image {
    // Note: this implies module names must be unique between user constraints/preferences.
    @NonNull
    private final Map<String,ConstraintModule> constraintsModules;
    @NonNull
    private final Map<String,PreferenceModule> preferenceModules;
    private final Set<SetModule> activeSets;
    private final Set<ParameterModule> activeParams;
    private final Set<VariableModule> activeVariables;
    @NonNull
    private final ModelInterface model;
    private @NonNull String name;
    private @NonNull String description;
    private final @NonNull LocalDateTime creationDate;

    /**
     * Constructs a new empty image, given a Model.
     * @param model The already initialized model.
     */
    public Image(@NonNull ModelInterface model,@NonNull String name,@NonNull String description) {
        this.activeParams = new HashSet<>();
        this.constraintsModules = new HashMap<>();
        this.preferenceModules = new HashMap<>();
        this.activeVariables = new HashSet<>();
        this.activeSets = new HashSet<>();
        this.name = name;
        this.description = description;
        this.creationDate = LocalDateTime.now();
        this.model = model;
    }

    public Image(@NonNull ImageDTO imageDTO) {
        this.activeParams = new HashSet<>();
        this.constraintsModules = new HashMap<>();
        this.preferenceModules = new HashMap<>();
        this.activeVariables = new HashSet<>();
        this.activeSets = new HashSet<>();
        this.model = new Model(imageDTO.code());
        this.name = imageDTO.name();
        this.description = imageDTO.description();
        this.creationDate = LocalDateTime.now();
        for (VariableDTO variableDTO: imageDTO.variables()){
            String variableName= variableDTO.identifier();
            Variable variable = model.getVariable(variableName);
            if(variable==null)
                throw new IllegalArgumentException("No variable with name: " + variableName);
            this.activeVariables.add(new VariableModule(variable.getName(),variableDTO.structure(), variableDTO.alias()));
        }
        for (ConstraintModuleDTO constraintModuleDTO : imageDTO.constraintModules()) {
            Set<Constraint> constraints = constraintModuleDTO.constraints().stream()
                    .map(constraintName -> {
                        Constraint constraint = model.getConstraint(constraintName);
                        if (constraint == null) {
                            throw new InvalidModelStateException("No constraint with name: " + constraintName);
                        }
                        return constraint;
                    }).collect(Collectors.toSet());
            this.constraintsModules.put(constraintModuleDTO.moduleName(), new ConstraintModule(
                    constraintModuleDTO.moduleName(),
                    constraintModuleDTO.description(),
                    constraints,
                    constraintModuleDTO.active()
            ));
        }
        for (PreferenceModuleDTO preferenceModuleDTO : imageDTO.preferenceModules()) {
            Set<Preference> preferences = preferenceModuleDTO.preferences().stream()
                    .map(preferenceName -> {
                        Preference preference = model.getPreference(preferenceName);
                        if (preference == null) {
                            throw new InvalidModelStateException("No preference with name: " + preferenceName);
                        }
                        return preference;
                    }).collect(Collectors.toSet());
            this.preferenceModules.put(preferenceModuleDTO.moduleName(), new PreferenceModule(
                    preferenceModuleDTO.moduleName(),
                    preferenceModuleDTO.description(),
                    preferences,
                    preferenceModuleDTO.scalar()
            ));
        }
        for (SetDTO setDTO: imageDTO.sets()){
            ModelSet modelSet= model.getSet(setDTO.setDefinition().name());
            if(modelSet==null)
                throw new InvalidModelStateException("No set with name: " + setDTO.setDefinition().name());
            for(String value: setDTO.values()) {
                if (!modelSet.isCompatible(value))
                    throw new InvalidModelStateException(String.format("Data mismatch in set %s. Expected: %s, Actual: %s", setDTO.setDefinition().name(),
                            modelSet.getDataType().toString(),
                            value));
            }
            activeSets.add(new SetModule(modelSet.getName(),setDTO.setDefinition().structure(),setDTO.setDefinition().alias(),modelSet.getData()));
        }
        for (ParameterDTO parameterDTO: imageDTO.parameters()){
            ModelParameter modelParameter= model.getParameter(parameterDTO.parameterDefinition().name());
            if(modelParameter==null)
                throw new InvalidModelStateException("No parameter with name: " + parameterDTO.parameterDefinition().name());
            if(!modelParameter.isCompatible(parameterDTO.value()))
                throw new InvalidModelStateException(String.format("Data mismatch in parameter %s. Expected: %s, Actual: %s",parameterDTO.parameterDefinition().name(),
                        modelParameter.getDataType().toString(),
                        parameterDTO.value()));
            activeParams.add(new ParameterModule(modelParameter.getName(),parameterDTO.parameterDefinition().structure(),parameterDTO.parameterDefinition().alias(),modelParameter.getData()));
        }
    }
    /**
     * Given source code and all Image components, creates an image object. This is intended for images loaded from persistence.
     * For runtime concerns, the Model object is created on get, meaning the source code isn't parsed until the Image needs it.
     *
     * @param code               The source code of the model.
     * @param constraintsModules The constraint modules to load.
     * @param preferenceModules  The preference modules to load.
     * @param activeSets         The active sets used in the model.
     * @param activeParams       The active parameters used in the model.
     * @param activeVariables    The active variables used in the model.
     */
    public Image (@NonNull String code,@NonNull  String name,@NonNull  String description,@NonNull  LocalDateTime creationDate,
                  @NonNull Set<ConstraintModule> constraintsModules, @NonNull Set<PreferenceModule> preferenceModules,
                  Set<SetModule> activeSets, Set<ParameterModule> activeParams, Set<VariableModule> activeVariables) {
        this.constraintsModules = constraintsModules.stream().collect(Collectors.toMap(ConstraintModule::getName, constraintModule -> constraintModule));
        this.preferenceModules = preferenceModules.stream().collect(Collectors.toMap(PreferenceModule::getName, preferenceModule -> preferenceModule));
        this.activeSets = activeSets;
        this.activeParams = activeParams;
        this.activeVariables = activeVariables;
        this.model = new ModelProxy(code);
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
    }

    /**
     * Overrides the image with new fields from the DTO data.
     * Ideally, this should be replaced with a diff,
     * i.e., an imageDiffDTO with data about changes only
     * Additional reasoning for why this is bad: if you only changed an alias, for example,
     * the Model object doesn't need to be loaded, since it isn't changed.
     * In this impl, the Model is always loaded. We want to avoid doing so since loading the model
     * means parsing the zpl code, which, as one can expect, is heavy.
     */
    public void override(@NonNull ImageDTO imageDTO) {
        this.constraintsModules.clear();
        this.preferenceModules.clear();
        this.activeSets.clear();
        this.activeParams.clear();
        this.activeVariables.clear();
        this.name = imageDTO.name();
        this.description = imageDTO.description();
        for (VariableDTO variableDTO: imageDTO.variables()){
            String variableName= variableDTO.identifier();
            Variable variable = model.getVariable(variableName);
            if(variable==null)
                throw new ClientSideError("No variable with name: " + variableName);
            this.activeVariables.add(new VariableModule(variable.getName(),variableDTO.structure(), variableDTO.alias()));
        }
        for (ConstraintModuleDTO constraintModuleDTO : imageDTO.constraintModules()) {
            Set<Constraint> constraints = constraintModuleDTO.constraints().stream()
                    .map(constraintName -> {
                        Constraint constraint = model.getConstraint(constraintName);
                        if (constraint == null) {
                            throw new ClientSideError("No constraint with name: " + constraintName);
                        }
                        return constraint;
                    }).collect(Collectors.toSet());
            this.constraintsModules.put(constraintModuleDTO.moduleName(), new ConstraintModule(
                    constraintModuleDTO.moduleName(),
                    constraintModuleDTO.description(),
                    constraints,
                    constraintModuleDTO.active()
            ));
        }
        for (PreferenceModuleDTO preferenceModuleDTO : imageDTO.preferenceModules()) {
            Set<Preference> preferences = preferenceModuleDTO.preferences().stream()
                    .map(preferenceName -> {
                        Preference preference = model.getPreference(preferenceName);
                        if (preference == null) {
                            throw new ClientSideError("No preference with name: " + preferenceName);
                        }
                        return preference;
                    }).collect(Collectors.toSet());
            this.preferenceModules.put(preferenceModuleDTO.moduleName(), new PreferenceModule(
                    preferenceModuleDTO.moduleName(),
                    preferenceModuleDTO.description(),
                    preferences,
                    preferenceModuleDTO.scalar()
            ));
        }
        for (SetDTO setDTO: imageDTO.sets()){
            ModelSet modelSet= model.getSet(setDTO.setDefinition().name());
            if(modelSet==null)
                throw new ClientSideError("No set with name: " + setDTO.setDefinition().name());
            if(!modelSet.isCompatible(setDTO.values()))
                throw new UserInputException(String.format("Data mismatch in set %s. Expected: %s, Actual: %s",setDTO.setDefinition().name(),
                        modelSet.getDataType().toString(),
                        String.join(",", setDTO.values())));
            activeSets.add(new SetModule(modelSet.getName(),setDTO.setDefinition().structure(),setDTO.setDefinition().alias(),setDTO.values()));
        }
        for (ParameterDTO parameterDTO: imageDTO.parameters()){
            ModelParameter modelParameter= model.getParameter(parameterDTO.parameterDefinition().name());
            if(modelParameter==null)
                throw new ClientSideError("No parameter with name: " + parameterDTO.parameterDefinition().name());
            if(!modelParameter.isCompatible(parameterDTO.value()))
                throw new UserInputException(String.format("Data mismatch in parameter %s. Expected: %s, Actual: %s",parameterDTO.parameterDefinition().name(),
                        modelParameter.getDataType().toString(),
                        parameterDTO.value()));
            activeParams.add(new ParameterModule(modelParameter.getName(),parameterDTO.parameterDefinition().structure(),parameterDTO.parameterDefinition().alias(),parameterDTO.value()));
        }
    }
    public void apply(@NonNull ImageConfigDTO config) {
        for(String prefModuleName: config.preferenceModulesScalars().keySet()){
            PreferenceModule prefModule = this.preferenceModules.get(prefModuleName);
            if(prefModule==null)
                throw new ClientSideError("No preference module with name: " + prefModuleName);
            prefModule.setScalar(config.preferenceModulesScalars().get(prefModuleName));
        }
        for(String constraintModuleName: config.enabledConstraintModules()){
            ConstraintModule constraintModule = this.constraintsModules.get(constraintModuleName);
            if(constraintModule==null)
                throw new ClientSideError("No constraint module with name: " + constraintModuleName);
            constraintModule.enable();
        }
    }
    public String getModifiedZimplCode(){
        Set<String> inactiveConstraints = this.constraintsModules.values().stream()
                .filter(constraintModule -> !constraintModule.isActive())
                .flatMap(module -> module.getConstraints().stream())
                .collect(Collectors.toSet());
        Map<String, Float> preferencesToScalars = this.preferenceModules.values().stream()
                .flatMap(module -> module.getPreferences().values().stream()
                        .map(preference -> Map.entry(preference.getName(), module.getScalar())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
        Map<String, List<String>> sets = this.activeSets.stream()
                .collect(Collectors.toMap(SetModule::getOriginalName, SetModule::getData));
        Map<String, String> params = this.activeParams.stream()
                .collect(Collectors.toMap(ParameterModule::getOriginalName, ParameterModule::getData));
        return model.writeToSource(sets,params,inactiveConstraints,preferencesToScalars);
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void addConstraintModule(@NonNull ConstraintModule module) {
        constraintsModules.put(module.getName(), module);
    }
    @SuppressWarnings("unused")
    public void addConstraint(@NonNull String moduleName, @NonNull String description) {
        constraintsModules.put(moduleName, new ConstraintModule(moduleName, description));
    }
    public void addConstraintModule(@NonNull String moduleName, @NonNull String description, @NonNull Collection<String> constraints) {
        HashSet<Constraint> modelConstraints = new HashSet<>();
        for (String name : constraints) {
            Constraint constraint = model.getConstraint(name);
            Objects.requireNonNull(constraint,"Invalid constraint name in add constraint in image");
            modelConstraints.add(constraint);
        }
        constraintsModules.put(moduleName, new ConstraintModule(moduleName, description, modelConstraints/*,inputSets,inputParams*/));
    }

    public void addPreferenceModule(@NonNull PreferenceModule module) {
        preferenceModules.put(module.getName(), module);
    }

    public void addPreferenceModule(@NonNull String moduleName, @NonNull String description, @NonNull Collection<String> preferences) {
        HashSet<Preference> modelPreferences = new HashSet<>();
        for (String name : preferences) {
            Preference preference = model.getPreference(name);
           Objects.requireNonNull(preference,"Invalid preference name in add preference module");
           modelPreferences.add(preference);
        }
        preferenceModules.put(moduleName, new PreferenceModule(moduleName, description,modelPreferences));
    }
    public ConstraintModule getConstraintModule(String name) {
        return constraintsModules.get(name);
    }
    public PreferenceModule getPreferenceModules(String name) {
        return preferenceModules.get(name);
    }
    @NonNull
    public Map<String, ConstraintModule> getConstraintsModules() {
        return constraintsModules;
    }
    @NonNull
    public Map<String, PreferenceModule> getPreferenceModules() {
        return preferenceModules;
    }

    @NonNull
    public Set<VariableModule> getActiveVariables () {
        return activeVariables;
    }

    @NonNull
    public ModelInterface getModel() {
        return this.model;
    }
    @NonNull
    public Set<SetModule> getActiveSets () {
        return activeSets;
    }
    @NonNull
    public Set<ParameterModule> getActiveParams () {
        return activeParams;
    }
    public String getSourceCode() {
        return model.getSourceCode();
    }


}
