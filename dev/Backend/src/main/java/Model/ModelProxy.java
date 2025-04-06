package Model;

import Exceptions.InternalErrors.BadRequestException;
import Exceptions.InternalErrors.ModelExceptions.ModelBuildException;
import Exceptions.InternalErrors.ModelExceptions.ZimplCompileError;
import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.OperationalElement;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Elements.Variable;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ModelProxy implements ModelInterface{
    @Value("${app.file.storage-dir}")
    private String storageDir;

    private final String code;
    private Model model;

    public ModelProxy(String code){
        this.code=code;
    }
    private Model getModel(){
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
                Path storagePath = Paths.get(appDir, storageDir);
                Files.createDirectories(storagePath);
                Path filePath = storagePath.resolve(UUID.randomUUID() + ".zpl");
                Files.writeString(filePath, code, StandardOpenOption.CREATE);
                this.model = new Model(filePath.toAbsolutePath().toString());
                return model;
            } catch (IOException e) {
                throw new ModelBuildException("I/O while creating model: " + e.getMessage());
            }
        }
        else {
            return model;
        }
    }

    @Override
    public String getSourceCode () {
        return code;
    }

    @Override
    public void appendToSet (ModelSet set, String value) {
        getModel().appendToSet(set,value);
    }

    @Override
    public void removeFromSet (ModelSet set, String value) {
        getModel().removeFromSet(set,value);
    }

    @Override
    public void setInput (ModelParameter identifier, String value) {
        getModel().setInput(identifier,value);
    }

    @Override
    public void setInput (ModelSet identifier, String[] values) {
        getModel().setInput(identifier,values);
    }

    @Override
    public List<String> getInput (ModelParameter parameter) {
        return getModel().getInput(parameter);
    }

    @Override
    public List<List<String>> getInput (ModelSet set) {
        return getModel().getInput(set);
    }

    @Override
    public void toggleFunctionality (OperationalElement operationalElement, boolean turnOn) {
        getModel().toggleFunctionality(operationalElement,turnOn);
    }

    @Override
    public boolean isCompiling (float timeout) {
        return getModel().isCompiling(timeout);
    }

    @Override
    public Solution solve (float timeout, String solutionFileSuffix) throws ZimplCompileError {
        return getModel().solve(timeout,solutionFileSuffix);
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
    public Collection<Preference> getPreferences () {
        return getModel().getPreferences();
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
}
