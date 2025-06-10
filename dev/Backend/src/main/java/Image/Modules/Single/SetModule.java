package Image.Modules.Single;

import Model.Data.Elements.Data.ModelSet;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;


public class SetModule {
    @NotNull
    private final ModelSet set;

    @NotNull
    private  String alias;

    public SetModule (@NotNull ModelSet set,@Nullable String alias) {
        this.set = set;
        if(alias==null) this.alias=set.getName();
        else this.alias = alias;
    }

    @SuppressWarnings("unused")
    public SetModule (@NotNull ModelSet set) {
        this.set = set;
        alias = set.getName();
    }
    public @NotNull ModelSet getSet() {
        return set;
    }

    public @NotNull String getAlias() {
        return alias;
    }
    public void setAlias(@NotNull String alias) {
        this.alias = alias;
    }
}
