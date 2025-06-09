package Model;

import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Elements.Variable;

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
    /*private Model getModel(){
        if(model==null) {
            try {
                // Get application directory
                String appDir;
                try {
                    URI uri = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
                    appDir = new File(uri).getParent();
                } catch (URISyntaxException e) {
                    appDir = System.getProperty("/tmp"); // Fallback
                }
                if (appDir == null) {
                    throw new ModelBuildException("Could not determine application directory.");
                }

                // Resolve the path relative to the JAR location
                String storageDir = "User/Model";
                Path storagePath = Paths.get(appDir, storageDir);
                Files.createDirectories(storagePath);
                Path filePath = storagePath.resolve(UUID.randomUUID() + ".zpl");
                Files.writeString(filePath, code, StandardOpenOption.CREATE);
                this.model = new Model(filePath.toAbsolutePath().toString());
                return model;
            } catch (IOException e) {
                throw new ModelBuildException("I/O error while creating model: " + e.getMessage());
            }
        }
        else {
            return model;
        }
    }*/

    /**
     * Get the model, create a new instance (and in turn, parse the model) if it doesn't exist.
     * @return new or existing model instance.
     */
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
    public Collection<Variable> getVariables (Collection<String> identifiers) {
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
    public String writeToSource(Set<ModelSet> sets, Set<ModelParameter> params, Set<Constraint> disabledConstraints, Set<Preference> preferencesScalars) {
        return getModel().writeToSource(sets,params,disabledConstraints,preferencesScalars);
    }

    @Override
    public String writeToSource(Set<ModelSet> sets, Set<ModelParameter> params, Set<Constraint> disabledConstraints, Map<String, Float> preferencesScalars) {
        return getModel().writeToSource(sets,params,disabledConstraints,preferencesScalars);
    }
}
