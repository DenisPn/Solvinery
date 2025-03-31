package Image.Modules.Operational;

public abstract class OperationalModule {


    public OperationalModule(String name, String description) {
        this.name = name;
        this.description = description;
    }
    private String name;
    private String description;
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
