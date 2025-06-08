package Model.Data.Elements.Operational;

import java.util.Objects;

public class Preference extends OperationalElement {
    private float scalar;

    public Preference (String body) {
        super(body);
        scalar=1;
    }
    @Deprecated(forRemoval = true)
    public Preference (String body,float scalar) {
        super(body);
        this.scalar=scalar;
    }
    @Deprecated(forRemoval = true)
    public void setScalar(float scalar) {
        if(scalar>=0 && scalar<=1)
            this.scalar = scalar;
        else throw new IllegalArgumentException("Scalar must be between 0 and 1");
    }
    @Deprecated(forRemoval = true)
    public float getScalar() {
        return scalar;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return super.equals(o); //scalar isn't checked
    }
    @Override
    public ElementType getType() {
        return ElementType.PREFERENCE;
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
