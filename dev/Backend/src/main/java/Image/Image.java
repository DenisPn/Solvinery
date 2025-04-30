package Image;

import java.util.*;
import java.util.stream.Collectors;

import Exceptions.InternalErrors.ModelExceptions.InvalidModelStateException;
import Exceptions.InternalErrors.ModelExceptions.Parsing.ParsingException;
import Exceptions.InternalErrors.ModelExceptions.ZimplCompileError;
import Image.Modules.Single.ParameterModule;
import Image.Modules.Single.SetModule;
import Image.Modules.Single.VariableModule;
import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Elements.Variable;
import groupId.DTO.Factories.RecordFactory;
import groupId.DTO.Records.Image.ConstraintModuleDTO;
import groupId.DTO.Records.Image.ImageDTO;
import groupId.DTO.Records.Image.PreferenceModuleDTO;
import groupId.DTO.Records.Image.SolutionDTO;
import groupId.DTO.Records.Model.ModelData.ParameterDTO;
import groupId.DTO.Records.Model.ModelData.SetDTO;
import groupId.DTO.Records.Model.ModelDefinition.ConstraintDTO;
import groupId.DTO.Records.Model.ModelDefinition.PreferenceDTO;
import Image.Modules.Grouping.ConstraintModule;
import Image.Modules.Grouping.PreferenceModule;
import Model.ModelProxy;
import Model.Model;
import Model.ModelInterface;
import Model.Solution;
import groupId.DTO.Records.Model.ModelDefinition.VariableDTO;

public class Image {
    // Note: this implies module names must be unique between user constraints/preferences.
    private final Map<String,ConstraintModule> constraintsModules;
    private final Map<String,PreferenceModule> preferenceModules;
    private final Set<SetModule> activeSets;
    private final Set<ParameterModule> activeParams;
    private final Set<VariableModule> activeVariables;
    private final ModelInterface model;

    /**
     * Constructs a new empty image, given a Model. It's implied from the existence of Model that there is a file on
     * In the filesystem with the source code on it.
     * @param model The already initialized model.
     */
    public Image(ModelInterface model) {
        this.activeParams = new HashSet<>();
        this.constraintsModules = new HashMap<>();
        this.preferenceModules = new HashMap<>();
        this.activeVariables = new HashSet<>();
        this.activeSets = new HashSet<>();
        this.model = model;
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
    public Image (String code, Set<ConstraintModule> constraintsModules, Set<PreferenceModule> preferenceModules, Set<SetModule> activeSets, Set<ParameterModule> activeParams, Set<VariableModule> activeVariables) {
        this.constraintsModules = constraintsModules.stream().collect(Collectors.toMap(ConstraintModule::getName, constraintModule -> constraintModule));
        this.preferenceModules = preferenceModules.stream().collect(Collectors.toMap(PreferenceModule::getName, preferenceModule -> preferenceModule));
        this.activeSets = activeSets;
        this.activeParams = activeParams;
        this.activeVariables = activeVariables;
        this.model = new ModelProxy(code);
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
            activeSets.add(new SetModule(modelSet,setDTO.setDefinition().alias()));

            model.setInput(modelSet,setDTO.values());
        }
        for (ParameterDTO parameterDTO: imageDTO.parameters()){
            ModelParameter modelParameter= model.getParameter(parameterDTO.parameterDefinition().name());
            activeParams.add(new ParameterModule(modelParameter,parameterDTO.parameterDefinition().alias()));

            model.setInput(modelParameter,parameterDTO.value());
        }
    }
    /**
     * Given path, created an Image and the Model inside it.
     * @param path path to file
     */
    public Image(String path) {
        this.activeParams = new HashSet<>();
        constraintsModules = new HashMap<>();
        preferenceModules = new HashMap<>();
        activeVariables = new HashSet<>();
        activeSets = new HashSet<>();
        this.model = new Model(path);
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

    /**
     * Solves the optimization problem using the provided timeout value.
     * This method compiles the model and attempts to find a solution within the given timeout limit.
     * If the solution is found, it is parsed and wrapped into a {@code SolutionDTO}.
     * In case of errors during compilation or solution parsing, appropriate runtime exceptions are thrown.
     *
     * @param timeout The maximum time, in seconds, allowed for solving the problem. Must be non-negative.
     * @return A {@code SolutionDTO} object containing the results of the solved optimization problem,
     *         including information such as whether the problem was solved, the solving time,
     *         the objective value, any error messages, and the solution variables.
     * @throws IllegalArgumentException if the timeout is negative.
     * @throws InvalidModelStateException if there is a compilation error in the model.
     * @throws ParsingException if an I/O exception occurs while parsing the solution file.
     */
    public SolutionDTO solve(int timeout){
        assert timeout >= 0 : "Timeout must be non-negative";
        try {
            Solution solution = model.solve(timeout, "SOLUTION");
            /*try {
                solution.parseSolution(model, activeVariables.getIdentifiers(), activeVariables.getAliases(), activeSets.getSetAliases());
            } catch (IOException e) {
                throw new ParsingException("IO exception while parsing solution file, message: " + e);
            }*/
            return RecordFactory.makeDTO(solution);
        }
        catch (ZimplCompileError e) {
            throw new InvalidModelStateException("Error while compiling model code before solve." +
                    " Error message: " + e.getMessage());
        }

    }
    public ModelInterface getModel() {
        return this.model;
    }
    @Deprecated
    public String getId() {
        // Do not use this! ID stored in controller, image not aware of its own ID.
        throw new UnsupportedOperationException("Unimplemented method 'getId'");
    }
    //Don't see it being used in the new version of our project, have a better solution
    @Deprecated
    public void reset(Map<String,Variable> variables,/* Collection<String> sets, Collection<String> params,*/Map<String,String> aliases) {
       /* constraintsModules.clear();
        preferenceModules.clear();
        this.activeVariables.override(variables*//*,sets,params*//*,aliases);*/
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
