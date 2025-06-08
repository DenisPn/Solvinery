package Image.Modules.Grouping;

import Model.Data.Elements.Operational.Constraint;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConstraintModule extends OperationalModule {
    /**
     * a constraint module, holding the user definition for a group of model constraints
     * (a group of subTo expressions in zimpl code)
     */

    private final Map<String, Constraint> constraints;
    private boolean active;
    public ConstraintModule(String name, String description) {
        super(name, description);
        this.constraints = new HashMap<>();
        active=true;
    }
    public ConstraintModule(String name, String description, Collection<Constraint> constraints/*, Collection<String> inputSets, Collection<String> inputParams*/) {
        super(name, description/*, inputSets, inputParams*/);
        this.constraints = new HashMap<>();
        for (Constraint constraint : constraints) {
            this.constraints.put(constraint.getName(), constraint);
        }
        this.active=true;
    }
    public ConstraintModule(String name, String description, Collection<Constraint> constraints, Boolean active) {
        super(name, description/*, inputSets, inputParams*/);
        this.constraints = new HashMap<>();
        for (Constraint constraint : constraints) {
            this.constraints.put(constraint.getName(), constraint);
        }
        if(active==null) this.active=true;
        else this.active=active;
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
    public void enable(){
        this.active=true;
    }
    public void disable(){
        this.active=false;
    }
    public boolean isActive(){
        return active;
    }
    public void removeConstraint(Constraint constraint){
        constraints.remove(constraint.getName());
    }
    public void removeConstraint(String identifier){
        constraints.remove(identifier);
    }
}
