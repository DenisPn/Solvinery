package Model.Data.Elements.Operational;


import java.util.Objects;

public class Constraint extends OperationalElement {

    public Constraint (String name) {
        super(name);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return super.equals(o);
    }

    @Override
    public ElementType getType() {
        return ElementType.CONSTRAINT;
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
