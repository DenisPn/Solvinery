package Model.Data.Elements;

public abstract class Element {
    private final String name;

    public Element (String name) {
        this.name = name;
    }

    public String getName () {
        return name;
    }
}
