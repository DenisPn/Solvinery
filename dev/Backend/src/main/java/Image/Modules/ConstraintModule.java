package Image.Modules;

import Model.*;
import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;

import java.util.*;

public class ConstraintModule extends Module{
    /**
     * a constraint module, holding the user definition for a group of model constraints
     * (a group of subTo expressions in zimpl code)
     */

    private final Map<String, Constraint> constraints;

    public ConstraintModule(String name, String description) {
        super(name, description);
        this.constraints = new HashMap<>();
    }
    public ConstraintModule(String name, String description, Collection<Constraint> constraints/*, Collection<String> inputSets, Collection<String> inputParams*/) {
        super(name, description/*, inputSets, inputParams*/);
        this.constraints = new HashMap<>();
        for (Constraint constraint : constraints) {
            this.constraints.put(constraint.getName(), constraint);
        }
    }
    /**
     * Fetch all ModelSets that are in use in any of the constraints in the module.
     * @return all sets that are part of any constraint in the module
     */
    @Override
    public Set<ModelSet> getInvolvedSets(){
        HashSet<ModelSet> involvedSets = new HashSet<>();
        /*for(Constraint constraint : constraints.values()){
            constraint.getPrimitiveSets(involvedSets);
        }*/
        return involvedSets;
    }

    @Override
    public Set<ModelParameter> getInvolvedParameters() {
        HashSet<ModelParameter> involvedParameters = new HashSet<>();
        /*for(Constraint preference : constraints.values()){
            preference.getPrimitiveParameters(involvedParameters);
        }*/
        return involvedParameters;
    }
    public Map<String, Constraint> getConstraints() {
        return constraints;
    }
    public Constraint getConstraint(String constraintName) {
        return constraints.get(constraintName);
    }
    public void addConstraint(Constraint constraint){
        constraints.put(constraint.getName(),constraint);
    }

    public void removeConstraint(Constraint constraint){
        constraints.remove(constraint.getName());
    }
    public void removeConstraint(String identifier){
        constraints.remove(identifier);
    }
}
