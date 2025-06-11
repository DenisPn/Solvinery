package Image.Modules.Grouping;


import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNull;

public abstract class OperationalModule {

    @NonNull
    private String name;
    @NonNull
    private String description;

    public OperationalModule(@NonNull String name, @NonNull String description) {
        this.name = name;
        this.description = description;

    }
    public @NonNull String getName() {
        return name;
    }
    public @NonNull String getDescription() {
        return description;
    }
    public void setName(@NonNull String name) {
        this.name = name;
    }
    public void setDescription(@NonNull String description) {
        this.description = description;
    }
}
