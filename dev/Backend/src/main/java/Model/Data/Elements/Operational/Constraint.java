package Model.Data.Elements.Operational;



public class Constraint extends OperationalElement {
    private boolean isOn;
    public Constraint (String name) {
        super(name);
        isOn=true;
    }
    public boolean isOn() {
        return isOn;
    }
    public void toggle(boolean toggle) {
        isOn = toggle;
    }
}
