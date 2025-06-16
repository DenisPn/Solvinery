package Model;

import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Elements.Variable;
import org.jetbrains.annotations.Nullable;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    @Nullable ModelParameter getParameter(String identifier);

    /**
     * Retrieves a constraint by its identifier.
     * 
     * @param identifier The constraint identifier
     * @return ModelConstraint object if found, null otherwise
     */
    @Nullable
    Constraint getConstraint(String identifier);

    /**
     * Retrieves all constraints loaded in the model
     * @return set of all constraints parsed from the model
     */
    Collection<Constraint> getConstraints();
    /**
     * Retrieves a preference by its identifier.
     * 
     * @param identifier The preference identifier
     * @return ModelPreference object if found, null otherwise
     */
    @Nullable
    Preference getPreference(String identifier);
    /**
     * Retrieves all preferences loaded in the model
     * @return set of all preferences parsed from the model
     */
    Collection<Preference> getModifiedPreferences();
    /**
     * Retrieves a variable by its identifier.
     * 
     * @param identifier The variable identifier
     * @return ModelVariable object if found, null otherwise
     */
    Variable getVariable(String identifier);

    Collection<Variable> getVariables();

    Collection<Variable> getVariables(Collection<String> identifiers);


    Collection<ModelSet> getSets();

    Collection<ModelParameter> getParameters();
    Set<String> getOriginalPreferences();
    //String writeToSource(Set<ModelSet> sets, Set<ModelParameter> params, Set<Constraint> disabledConstraints, Map<String,Float> preferencesScalars);
    String writeToSource(@NonNull Map<String, List<String>> sets, @NonNull Map<String,String> params, Set<String> disabledConstraints, Map<String,Float> preferencesScalars);

}