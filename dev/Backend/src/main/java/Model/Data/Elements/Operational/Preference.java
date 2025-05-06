package Model.Data.Elements.Operational;

public class Preference extends OperationalElement {
    private float scalar;
    public Preference (String name) {
        super(name);
        scalar=1;
    }

    public void setScalar(float scalar) {
        if(scalar>=0 && scalar<=1)
            this.scalar = scalar;
        else throw new IllegalArgumentException("Scalar must be between 0 and 1");
    }

    public float getScalar() {
        return scalar;
    }
}
