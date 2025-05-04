package Model.Data.Elements.Data;

import Model.Data.Elements.Element;
import Model.Data.Types.ModelType;
import jakarta.persistence.MappedSuperclass;

public abstract class DataElement extends Element {
    protected final ModelType type;
   // protected final String alias;


    public DataElement (String name, ModelType type) {
        super(name);
        this.type = type;
       // this.alias = null;
    }
   /* public DataElement (String name, ModelType type,String alias) {
        super(name);
        this.type = type;
        this.alias = alias;
    }*/

    public boolean isCompatible (DataElement element){
        return this.type.isCompatible(element.getType());
    }
    public boolean isCompatible (String element){
        return this.type.isCompatible(element);
    }

    public ModelType getType () {
        return type;
    }
/*
    public String getAlias() {
        return alias;
    }
    public boolean hasAlias() {
        return alias!=null;
    }*/
}
