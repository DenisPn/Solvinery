package Image.Modules.Single;

import Model.Data.Elements.Data.ModelSet;
import jakarta.annotation.Nullable;
import org.springframework.lang.NonNull;

import java.util.List;


public class SetModule extends DataModule {
    @NonNull
    private final String name;
    @NonNull
    private final List<String> data;

    public SetModule (@NonNull String name, @NonNull List<String> structure, @Nullable String alias, @NonNull List<String> data) {
        super(structure, alias == null ? name : alias);
        this.name = name;
        this.data = data;
    }
    public SetModule (@NonNull String name, @NonNull String structure, @Nullable String alias, @NonNull List<String> data) {
        super(structure, alias == null ? name : alias);
        this.name = name;
        this.data = data;
    }

    @NonNull
    public String getOriginalName() {
        return name;
    }

    @NonNull
    public List<String> getData() {
        return data;
    }
}
