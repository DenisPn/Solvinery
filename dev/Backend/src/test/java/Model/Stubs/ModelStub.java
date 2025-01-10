package Model.Stubs;

import Model.*;

import java.io.IOException;

public class ModelStub implements ModelInterface {
    Model RealModel;
    public ModelStub(String sourceFilePath) throws IOException {
        RealModel = new Model(sourceFilePath);
    }

    @Override
    public void appendToSet(ModelSet set, String value) throws Exception {

    }

    @Override
    public void removeFromSet(ModelSet set, String value) throws Exception {

    }

    @Override
    public void setInput(ModelInput identifier, String value) throws Exception {

    }

    @Override
    public void toggleFunctionality(ModelFunctionality mf, boolean turnOn) {

    }

    @Override
    public boolean isCompiling(float timeout) {
        return false;
    }

    @Override
    public Solution solve(float timeout) {
        return null;
    }

    @Override
    public ModelSet getSet(String identifier) {
        return null;
    }

    @Override
    public ModelParameter getParameter(String identifier) {
        return null;
    }

    @Override
    public ModelConstraint getConstraint(String identifier) {
        return null;
    }

    @Override
    public ModelPreference getPreference(String identifier) {
        return null;
    }

    @Override
    public ModelVariable getVariable(String identifier) {
        return null;
    }
}
