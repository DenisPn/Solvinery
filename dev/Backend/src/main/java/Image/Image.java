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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Image {
    // Note: this implies module names must be unique between user constraints/preferences.
    private final Map<String,ConstraintModule> constraintsModules;
    private final Map<String,PreferenceModule> preferenceModules;
    private final Set<SetModule> activeSets;
    private final Set<ParameterModule> activeParams;
    private final Set<VariableModule> activeVariables;
    private final ModelInterface model;
    private final String name;
    private final String description;
    private final LocalDateTime creationDate;

    /**
     * Constructs a new empty image, given a Model.
     * @param model The already initialized model.
     */
    public Image(ModelInterface model,String name,String description) {
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

    public Image(ImageDTO imageDTO) {
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
                    constraints
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
                    preferences
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
    public Image (String code,String name,String description,LocalDateTime creationDate, Set<ConstraintModule> constraintsModules, Set<PreferenceModule> preferenceModules, Set<SetModule> activeSets, Set<ParameterModule> activeParams, Set<VariableModule> activeVariables) {
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
     * Given code, created an Image and the Model inside it.
     * @param code source zpl code
     */
    @Deprecated(forRemoval = true)
    public Image(String code) {
        this.activeParams = new HashSet<>();
        constraintsModules = new HashMap<>();
        preferenceModules = new HashMap<>();
        activeVariables = new HashSet<>();
        activeSets = new HashSet<>();
        this.model = new Model(code);
        creationDate = null;
        this.name = null;
        this.description = null;
    }
    /**
     * Overrides the image with new fields from the DTO data.
     * Ideally this should be replaced with a diff,
     * i.e., an imageDiffDTO with data about changes only
     * Additional reasoning for why this is bad: if you only changed an alias, for example,
     * the Model object doesn't need to be loaded, since it isn't changed.
     * in this impl, the Model is always loaded. We want to avoid doing so since loading the model
     * means parsing the zpl code, which, as one can expect, is heavy.
     */
    public void override(ImageDTO imageDTO) {
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
                    constraints
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
                    preferences
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void addConstraintModule(ConstraintModule module) {
        constraintsModules.put(module.getName(), module);
    }
    public void addConstraint(String moduleName, String description) {
        constraintsModules.put(moduleName, new ConstraintModule(moduleName, description));
    }
    public void addConstraintModule(String moduleName, String description, Collection<String> constraints) {
        HashSet<Constraint> modelConstraints = new HashSet<>();
        for (String name : constraints) {
            Constraint constraint = model.getConstraint(name);
            Objects.requireNonNull(constraint,"Invalid constraint name in add constraint in image");
            modelConstraints.add(constraint);
        }
        constraintsModules.put(moduleName, new ConstraintModule(moduleName, description, modelConstraints/*,inputSets,inputParams*/));
    }

    public void addPreferenceModule(PreferenceModule module) {
        preferenceModules.put(module.getName(), module);
    }
    public void addPreference(String moduleName, String description) {
        preferenceModules.put(moduleName, new PreferenceModule(moduleName, description));
    }
    public void addPreferenceModule(String moduleName, String description, Collection<String> preferences) {
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
    public Map<String, ConstraintModule> getConstraintsModules() {
        return constraintsModules;
    }
    public Map<String, PreferenceModule> getPreferenceModules() {
        return preferenceModules;
    }

    public void addConstraint(String moduleName, ConstraintDTO constraint) {
        if(!constraintsModules.containsKey(moduleName))
            throw new IllegalArgumentException("No constraint module with name: " + moduleName);
        constraintsModules.get(moduleName).addConstraint(model.getConstraint(constraint.identifier()));
    }
    public void removeConstraint(String moduleName, ConstraintDTO constraint) {
        if(!constraintsModules.containsKey(moduleName))
            throw new IllegalArgumentException("No constraint module with name: " + moduleName);
        constraintsModules.get(moduleName).removeConstraint(model.getConstraint(constraint.identifier()));
    }
    public void addPreference(String moduleName, PreferenceDTO preferenceDTO) {
        if(!preferenceModules.containsKey(moduleName))
            throw new IllegalArgumentException("No preference module with name: " + moduleName);
        preferenceModules.get(moduleName).addPreference(model.getPreference(preferenceDTO.identifier()));
    }
    public void removePreference(String moduleName, PreferenceDTO preferenceDTO) {
        if(!preferenceModules.containsKey(moduleName))
            throw new IllegalArgumentException("No preference module with name: " + moduleName);
        preferenceModules.get(moduleName).removePreference(model.getPreference(preferenceDTO.identifier()));
    }
    public Set<VariableModule> getActiveVariables () {
        return activeVariables;
    }
    public void addVariable(String name) {
        Variable varName= model.getVariable(name);
        if(varName==null)
            throw new IllegalArgumentException("No variable with name: " + name);
        activeVariables.add(new VariableModule(varName));
    }

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
