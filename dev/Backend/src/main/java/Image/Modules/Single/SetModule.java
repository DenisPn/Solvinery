package Image.Modules.Single;

import Model.Data.Elements.Data.ModelSet;

public class SetModule {
    private final ModelSet Set;
    private  String alias;

    public SetModule (ModelSet set, String alias) {
        Set = set;
        this.alias = alias;
    }

    public SetModule (ModelSet set) {
        Set = set;
        alias = set.getName();
    }
    public ModelSet getSet() {
        return Set;
    }
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
}
