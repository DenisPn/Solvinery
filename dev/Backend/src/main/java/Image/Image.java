package Image;

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
    private final @NonNull String name;
    private final @NonNull String description;
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
            this.activeVariables.add(new VariableModule(variable, variableDTO.alias()));
        }
        for (ConstraintModuleDTO constraintModuleDTO : imageDTO.constraintModules()) {
            Set<Constraint> constraints = constraintModuleDTO.constraints().stream()
                    .map(constraintName -> {
                        Constraint constraint = model.getConstraint(constraintName);
                        if (constraint == null) {
                            throw new IllegalArgumentException("No constraint with name: " + constraintName);
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
                            throw new IllegalArgumentException("No preference with name: " + preferenceName);
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
            //modelSet.setData(setDTO.values());
            activeSets.add(new SetModule(modelSet,setDTO.setDefinition().alias()));
            //model.setInput(modelSet);
        }
        for (ParameterDTO parameterDTO: imageDTO.parameters()){
            ModelParameter modelParameter= model.getParameter(parameterDTO.parameterDefinition().name());
            //modelParameter.setData(parameterDTO.value());
            activeParams.add(new ParameterModule(modelParameter,parameterDTO.parameterDefinition().alias()));
            //model.setInput(modelParameter);
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
        
        for (VariableDTO variableDTO: imageDTO.variables()){
            String variableName= variableDTO.identifier();
            Variable variable = model.getVariable(variableName);
            if(variable==null)
                throw new IllegalArgumentException("No variable with name: " + variableName);
            this.activeVariables.add(new VariableModule(variable, variableDTO.alias()));
        }
        for (ConstraintModuleDTO constraintModuleDTO : imageDTO.constraintModules()) {
            Set<Constraint> constraints = constraintModuleDTO.constraints().stream()
                    .map(constraintName -> {
                        Constraint constraint = model.getConstraint(constraintName);
                        if (constraint == null) {
                            throw new IllegalArgumentException("No constraint with name: " + constraintName);
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
                            throw new IllegalArgumentException("No preference with name: " + preferenceName);
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
            modelSet.setData(setDTO.values());
            activeSets.add(new SetModule(modelSet,setDTO.setDefinition().alias()));
           // model.setInput(modelSet);
        }
        for (ParameterDTO parameterDTO: imageDTO.parameters()){
            ModelParameter modelParameter= model.getParameter(parameterDTO.parameterDefinition().name());
            modelParameter.setData(parameterDTO.value());
            activeParams.add(new ParameterModule(modelParameter,parameterDTO.parameterDefinition().alias()));
            //model.setInput(modelParameter);
        }
    }
    public void apply(@NonNull ImageConfigDTO config) {
        for(String prefModuleName: config.preferenceModulesScalars().keySet()){
            PreferenceModule prefModule = this.preferenceModules.get(prefModuleName);
            if(prefModule==null)
                throw new IllegalArgumentException("No preference module with name: " + prefModuleName);
            prefModule.setScalar(config.preferenceModulesScalars().get(prefModuleName));
        }
        for(String constraintModuleName: config.enabledConstraintModules()){
            ConstraintModule constraintModule = this.constraintsModules.get(constraintModuleName);
            if(constraintModule==null)
                throw new IllegalArgumentException("No constraint module with name: " + constraintModuleName);
            constraintModule.enable();
        }
    }
    public String getModifiedZimplCode(){
        Set<Constraint> inactiveConstraints = this.constraintsModules.values().stream()
                .filter(constraintModule -> !constraintModule.isActive())
                .flatMap(module -> module.getConstraints().values().stream())
                .collect(Collectors.toSet());
        Map<String, Float> preferencesToScalars = this.preferenceModules.values().stream()
                .flatMap(module -> module.getPreferences().values().stream()
                        .map(preference -> Map.entry(preference.getName(), module.getScalar())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
        Set<ModelSet> sets= this.activeSets.stream().map(SetModule::getSet).collect(Collectors.toSet());
        Set<ModelParameter> params= this.activeParams.stream().map(ParameterModule::getParameter).collect(Collectors.toSet());
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
    @SuppressWarnings("unused")
    public void addPreference(@NonNull String moduleName, @NonNull String description) {
        preferenceModules.put(moduleName, new PreferenceModule(moduleName, description));
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
    @SuppressWarnings("unused")
    public void addConstraint(String moduleName, @NonNull ConstraintDTO constraint) {
        if(!constraintsModules.containsKey(moduleName))
            throw new IllegalArgumentException("No constraint module with name: " + moduleName);
        constraintsModules.get(moduleName).addConstraint(model.getConstraint(constraint.identifier()));
    }
    @SuppressWarnings("unused")
    public void removeConstraint(String moduleName, @NonNull ConstraintDTO constraint) {
        if(!constraintsModules.containsKey(moduleName))
            throw new IllegalArgumentException("No constraint module with name: " + moduleName);
        constraintsModules.get(moduleName).removeConstraint(model.getConstraint(constraint.identifier()));
    }
    @SuppressWarnings("unused")
    public void addPreference(String moduleName, @NonNull PreferenceDTO preferenceDTO) {
        if(!preferenceModules.containsKey(moduleName))
            throw new IllegalArgumentException("No preference module with name: " + moduleName);
        preferenceModules.get(moduleName).addPreference(model.getPreference(preferenceDTO.identifier()));
    }
    @SuppressWarnings("unused")
    public void removePreference(String moduleName, @NonNull PreferenceDTO preferenceDTO) {
        if(!preferenceModules.containsKey(moduleName))
            throw new IllegalArgumentException("No preference module with name: " + moduleName);
        preferenceModules.get(moduleName).removePreference(model.getPreference(preferenceDTO.identifier()));
    }
    public Set<VariableModule> getActiveVariables () {
        return activeVariables;
    }
    @SuppressWarnings("unused")
    public void addVariable(String name) {
        Variable varName= model.getVariable(name);
        if(varName==null)
            throw new IllegalArgumentException("No variable with name: " + name);
        activeVariables.add(new VariableModule(varName));
    }

    @NonNull
    public ModelInterface getModel() {
        return this.model;
    }

    public Set<SetModule> getActiveSets () {
        return activeSets;
    }

    public Set<ParameterModule> getActiveParams () {
        return activeParams;
    }
    public String getSourceCode() {
        return model.getSourceCode();
    }


}
