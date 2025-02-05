package Image.Modules;

import Model.*;

import java.util.*;

public class ConstraintModule extends Module{
    /**
     * a constraint module, holding the user definition for a group of model constraints
     * (a group of subTo expressions in zimpl code)
     */

    private final Map<String,ModelConstraint> constraints;

    public ConstraintModule(String name, String description) {
        super(name, description);
        this.constraints = new HashMap<>();
    }
    public ConstraintModule(String name, String description, Collection<ModelConstraint> constraints, Collection<String> inputSets, Collection<String> inputParams) {
        super(name, description, inputSets, inputParams);
        this.constraints = new HashMap<>();
        for (ModelConstraint constraint : constraints) {
            this.constraints.put(constraint.getIdentifier(), constraint);
        }
    }
    /**
     * Fetch all ModelSets that are in use in any of the constraints in the module.
     * @return all sets that are part of any constraint in the module
     */
    @Override
    public Set<ModelSet> getInvolvedSets(){
        HashSet<ModelSet> involvedSets = new HashSet<>();
        for(ModelConstraint constraint : constraints.values()){
            constraint.getPrimitiveSets(involvedSets);
        }
        return involvedSets;
    }

    @Override
    public Set<ModelParameter> getInvolvedParameters() {
        HashSet<ModelParameter> involvedParameters = new HashSet<>();
        for(ModelConstraint preference : constraints.values()){
            preference.getPrimitiveParameters(involvedParameters);
        }
        return involvedParameters;
    }
    public Map<String, ModelConstraint> getConstraints() {
        return constraints;
    }
    public ModelConstraint getConstraint(String constraintName) {
        return constraints.get(constraintName);
    }
    public void addConstraint(ModelConstraint constraint){
        constraints.put(constraint.getIdentifier(),constraint);
    }

    public void removeConstraint(ModelConstraint constraint){
        constraints.remove(constraint.getIdentifier());
    }
    public void removeConstraint(String identifier){
        constraints.remove(identifier);
    }
}
