package Model;

import java.util.Collection;
import java.util.List;

import Exceptions.InternalErrors.ModelExceptions.ZimplCompileError;
import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.OperationalElement;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Elements.Variable;

/**
 * Interface defining the public API for interacting with a mathematical optimization model.
 * This interface provides methods for managing sets, parameters, constraints, preferences,
 * and variables within the model, as well as solving and compiling operations.
 */
public interface ModelInterface {
    String getSourceCode();
    /**
     * Appends a value to a specified set in the model.
     * 
     * @param set The set to append to
     * @param value The value to append
     */
    void appendToSet(ModelSet set, String value);

    /**
     * Removes a value from a specified set in the model.
     * 
     * @param set The set to remove from
     * @param value The value to remove
     */
    void removeFromSet(ModelSet set, String value);

    /**
     * Sets the value of a model input (parameter).
     * 
     * @param identifier The input identifier
     */
    void setInput(ModelParameter identifier);

    /**
     * Sets the value of a model input (set).
     * 
     * @param identifier The input identifier
     */
    void setInput(ModelSet identifier);

    /**
     * Retrieves the input values associated with a given model parameter.
     *
     * @param parameter The model parameter whose input values are to be retrieved.
     * @return A list of input values associated with the specified model parameter.
     */
    List<String> getInput(ModelParameter parameter);

    /**
     * Retrieves the input values associated with a given model set.
     *
     * @param set The model set whose input values are to be retrieved.
     * @return A list of input values associated with the specified model set.
     */
    List<List<String>> getInput(ModelSet set);

    
    /**
     * Toggles a model functionality on or off.
     * 
     * @param turnOn true to enable, false to disable
     */
    void toggleFunctionality(OperationalElement operationalElement, boolean turnOn);

    /**
     * Checks if the model compiles successfully.
     * 
     * @param timeout Maximum time in seconds to wait for compilation
     * @return true if compilation succeeds, false otherwise
     */
    boolean isCompiling(float timeout);

    /**
     * Solves the model and returns the solution.
     * 
     * @param timeout Maximum time in seconds to wait for solving
     * @return Solution object if solving succeeds, null otherwise
     */
    @Deprecated
    Solution solve(float timeout, String solutionFileSuffix) throws ZimplCompileError;

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
     * Commits all changes done in the image into zimpl code and returns the code.
     * @return zimpl code, with all changes done in the image.
     */
    String getCode();

}