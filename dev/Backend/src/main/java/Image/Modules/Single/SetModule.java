package Image.Modules.Single;

import Model.Data.Elements.Data.ModelSet;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNull;

import java.util.List;


public class SetModule {
    @NonNull
    private final ModelSet set;
    @NonNull
    private final List<String> typeAlias;
    @NonNull
    private  String alias;

    public SetModule (@NonNull ModelSet set, @Nullable String alias, @Nullable List<String> typeAlias) {
        this.set = set;
        if(alias==null) this.alias=set.getName();
        else this.alias = alias;
        List<String> types = set.getDataType().typeList();
        if(typeAlias==null)
            this.typeAlias=types;
        else if(types.size()!=typeAlias.size())
            throw new IllegalArgumentException("Type alias and set type do not match");
        else
            this.typeAlias = typeAlias;
    }

    public SetModule (@NonNull ModelSet set,@Nullable String alias) {
        this.set = set;
        if(alias==null) this.alias=set.getName();
        else this.alias = alias;
        this.typeAlias = set.getDataType().typeList();
    }
    @SuppressWarnings("unused")
    public SetModule (@NonNull ModelSet set) {
        this.set = set;
        alias = set.getName();
        this.typeAlias = set.getDataType().typeList();
    }
    @NonNull
    public String getOriginalName() {
        return set.getName();
    }
    @NonNull
    public List<String> getOriginalTypes() {
        return set.getDataType().typeList();
    }
    @NonNull
    public List<String> getTypes() {
        return typeAlias;
    }
    @NonNull
    public String getTypeString() {
        if(typeAlias.size() == 1) return typeAlias.getFirst();
        else return "<" + String.join(",",typeAlias) + ">";
    }
    @Deprecated(forRemoval = true)
    public @NonNull ModelSet getSet() {
        return set;
    }
    public @NotNull String getAlias() {
        return alias;
    }
    public List<String> getData() {
        return set.getData();
    }
    public void setAlias(@NonNull String alias) {
        this.alias = alias;
    }
}
