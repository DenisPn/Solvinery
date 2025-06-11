package Model.Data.Elements.Operational;


import org.jetbrains.annotations.Nullable;
import org.springframework.lang.NonNull;

import java.util.Objects;

public class Constraint extends OperationalElement {

    public Constraint (String name) {
        super(name);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return super.equals(o);
    }

    @NonNull
    @Override
    public ElementType getType() {
        return ElementType.CONSTRAINT;
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
