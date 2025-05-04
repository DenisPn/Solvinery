package Image.Modules.Grouping;

public abstract class OperationalModule {


    private String name;
    private String description;

    public OperationalModule(String name, String description) {
        this.name = name;
        this.description = description;

    }
    // private boolean isActive;
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
