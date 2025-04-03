package Image;

import java.io.IOException;
import java.util.*;

import Exceptions.InternalErrors.ModelExceptions.InvalidModelStateException;
import Exceptions.InternalErrors.ModelExceptions.Parsing.ParsingException;
import Exceptions.InternalErrors.ModelExceptions.ZimplCompileError;
import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Elements.Variable;
import groupId.DTO.Factories.RecordFactory;
import groupId.DTO.Records.Image.SolutionDTO;
import groupId.DTO.Records.Model.ModelDefinition.ConstraintDTO;
import groupId.DTO.Records.Model.ModelDefinition.PreferenceDTO;
import Image.Modules.Operational.ConstraintModule;
import Image.Modules.Operational.PreferenceModule;
import Model.Model;
import Model.ModelInterface;
import Model.Solution;

public class Image {
    // Note: this implies module names must be unique between user constraints/preferences.
    private final HashMap<String,ConstraintModule> constraintsModules;
    private final HashMap<String,PreferenceModule> preferenceModules;
    private final Set<ModelSet> activeSets;
    private final Set<ModelParameter> activeParams;
    private final Set<Variable> activeVariables;
    private final ModelInterface model;
    public Image(ModelInterface model) {
        this.activeParams = new HashSet<>();
        constraintsModules = new HashMap<>();
        preferenceModules = new HashMap<>();
        activeVariables = new HashSet<>();
        activeSets = new HashSet<>();
        this.model = model;
    }
    public Image(String path) throws IOException {
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
    public HashMap<String, ConstraintModule> getConstraintsModules() {
        return constraintsModules;
    }
    public HashMap<String, PreferenceModule> getPreferenceModules() {
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
    public Set<Variable> getActiveVariables () {
        return activeVariables;
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
    
}
