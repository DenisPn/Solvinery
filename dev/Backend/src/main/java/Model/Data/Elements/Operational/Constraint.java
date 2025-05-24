package Model.Data.Elements.Operational;


import java.util.Objects;

public class Constraint extends OperationalElement {
    private boolean isOn;
    public Constraint (String name) {
        super(name);
        isOn=true;
    }
    public Constraint (String name,boolean toggle) {
        super(name);
        isOn=toggle;
    }
    public boolean isOn() {
        return isOn;
    }
    public void toggle(boolean toggle) {
        isOn = toggle;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Constraint that = (Constraint) o;
        return isOn == that.isOn;
    }
    @Override
    public ElementType getType() {
        return ElementType.CONSTRAINT;
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isOn);
    }
}
