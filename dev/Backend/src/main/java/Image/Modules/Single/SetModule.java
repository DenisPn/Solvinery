package Image.Modules.Single;

import Model.Data.Elements.Data.ModelSet;

public class SetModule {
    private final ModelSet set;
    private  String alias;

    public SetModule (ModelSet set, String alias) {
        this.set = set;
        this.alias = alias;
    }

    public SetModule (ModelSet set) {
        this.set = set;
        alias = set.getName();
    }
    public ModelSet getSet() {
        return set;
    }
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
}
