package Model;

import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Elements.Variable;

import java.util.Collection;
import java.util.Set;

/**
 * Interface defining the public API for interacting with a mathematical optimization model.
 * This interface provides methods for managing sets, parameters, constraints, preferences,
 * and variables within the model, as well as solving and compiling operations.
 */
public interface ModelInterface {
    String getSourceCode();


    /**
     * Retrieves a set by its identifier.
     * 
     * @param identifier The set identifier
     * @return ModelSet object if found, null otherwise
     */
    ModelSet getSet(String identifier);

    /**
     * Retrieves a parameter by its identifier.
     * 
     * @param identifier The parameter identifier
     * @return ModelParameter object if found, null otherwise
     */
    ModelParameter getParameter(String identifier);

    /**
     * Retrieves a constraint by its identifier.
     * 
     * @param identifier The constraint identifier
     * @return ModelConstraint object if found, null otherwise
     */
    Constraint getConstraint(String identifier);

    /**
     * Retrieves an all constraints loaded in the model
     * @return set of all constraints parsed from the model
     */
    Collection<Constraint> getConstraints();
    /**
     * Retrieves a preference by its identifier.
     * 
     * @param identifier The preference identifier
     * @return ModelPreference object if found, null otherwise
     */
    Preference getPreference(String identifier);
    /**
     * Retrieves a all preferences loaded in the model
     * @return set of all preferences parsed from the model
     */
    Collection<Preference> getPreferences();
    /**
     * Retrieves a variable by its identifier.
     * 
     * @param identifier The variable identifier
     * @return ModelVariable object if found, null otherwise
     */
    Variable getVariable(String identifier);

    Collection<Variable> getVariables();

    Collection<Variable> getVariables(Collection<String> identifiers);


    public Collection<ModelSet> getSets();

    public Collection<ModelParameter> getParameters();

    /**
     * Write into a source file all the changed in input and return code after write.
     * @param sets valiues to write into sets. Write always overrides current data.
     * @param params parameter values to write into parameters. Write always overrides current data.
     * @param disabledConstraints constraints to disable.
     * @param preferencesScalars preferences that need their scalar to change. in practice changes the parameter of the preference's scalar.
     * @return zimpl code, with all changes done in the image.
     */
    String writeToSource(Set<ModelSet> sets, Set<ModelParameter> params, Set<Constraint> disabledConstraints, Set<Preference> preferencesScalars);

}