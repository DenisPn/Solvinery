package Model;

import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Elements.Variable;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Designed for lazy loading of the model class to avoid file parsing when it's unneeded.
 */
public class ModelProxy implements ModelInterface{

    private final String code;
    private Model model;

    public ModelProxy(String code){
        this.code=code;
    }

    /**
     * Get the model, create a new instance (and in turn, parse the model) if it doesn't exist.
     * @return new or existing model instance.
     */
    @NonNull
    private Model getModel(){
        if(model==null) {
                this.model = new Model(code);
        }
        return model;
    }

    @Override
    public String getSourceCode () {
        return code;
    }

    @Override
    public ModelSet getSet (String identifier) {
        return getModel().getSet(identifier);
    }

    @Override
    public ModelParameter getParameter (String identifier) {
       return getModel().getParameter(identifier);
    }

    @Override
    public Constraint getConstraint (String identifier) {
        return getModel().getConstraint(identifier);
    }

    @Override
    public Collection<Constraint> getConstraints () {
        return getModel().getConstraints();
    }

    @Override
    public Preference getPreference (String identifier) {
        return getModel().getPreference(identifier);
    }

    @Override
    public Collection<Preference> getModifiedPreferences() {
        return getModel().getModifiedPreferences();
    }

    @Override
    public Variable getVariable (String identifier) {
        return getModel().getVariable(identifier);
    }

    @Override
    public Collection<Variable> getVariables () {
        return getModel().getVariables();
    }

    @Override
    public Collection<Variable> getVariables (@NonNull Collection<String> identifiers) {
        return getModel().getVariables(identifiers);
    }

    @Override
    public Collection<ModelSet> getSets () {
        return getModel().getSets();
    }

    @Override
    public Collection<ModelParameter> getParameters () {
        return getModel().getParameters();
    }

    @Override
    public String writeToSource(@NonNull Set<ModelSet> sets, @NonNull Set<ModelParameter> params, @NonNull Set<Constraint> disabledConstraints, @NonNull Map<String, Float> preferencesScalars) {
        return getModel().writeToSource(sets,params,disabledConstraints,preferencesScalars);
    }
}
