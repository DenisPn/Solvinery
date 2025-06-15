package Image.Modules.Single;

import Model.Data.Elements.Data.ModelParameter;
import jakarta.annotation.Nullable;
import org.springframework.lang.NonNull;

import java.util.List;

public class ParameterModule extends DataModule {
    @NonNull
    private final String name;
    @NonNull
    private final String data;


    public ParameterModule (@NonNull String name, @NonNull String structure, @Nullable String alias, @NonNull String data) {
        super(structure, alias == null ? name : alias);
        this.name = name;
        this.data = data;
    }

    @NonNull
    public String getOriginalName(){
        return name;
    }

    @NonNull
    public String getData(){
        return data;
    }

}
