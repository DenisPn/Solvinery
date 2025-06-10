package Image.Modules.Grouping;

import Model.Data.Elements.Operational.Constraint;
import org.jetbrains.annotations.Nullable;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
/**
 * a constraint module, holding the user definition for a group of model constraints
 * (a group of subTo expressions in zimpl code)
 */
public class ConstraintModule extends OperationalModule {
    private static final boolean DEFAULT_STATE=false;
    @NonNull
    private final Map< String, Constraint> constraints;
    private boolean active;
    public ConstraintModule(@NonNull String name,@NonNull String description) {
        super(name, description);
        this.constraints = new HashMap<>();
        this.active=DEFAULT_STATE;
    }
    public ConstraintModule(@NonNull String name,@NonNull String description, @NonNull Collection< Constraint> constraints) {
        super(name, description);
        this.constraints = new HashMap<>();
        for (Constraint constraint : constraints) {
            this.constraints.put(constraint.getName(), constraint);
        }
        this.active=DEFAULT_STATE;
    }
    public ConstraintModule(@NonNull String name, @NonNull String description, @NonNull Collection< Constraint> constraints, @Nullable Boolean active) {
        super(name, description);
        this.constraints = new HashMap<>();
        for (Constraint constraint : constraints) {
            this.constraints.put(constraint.getName(), constraint);
        }
        if(active==null) this.active=DEFAULT_STATE;
        else this.active=active;
    }
    public @NonNull Map<String, Constraint> getConstraints() {
        return constraints;
    }

    public void addConstraint(@NonNull Constraint constraint){
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
    public void removeConstraint(@NonNull Constraint constraint){
        constraints.remove(constraint.getName());
    }
    public void removeConstraint(@NonNull String identifier){
        constraints.remove(identifier);
    }
}
