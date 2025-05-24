package Model.Data.Elements.Data;

import Model.Data.Elements.Element;
import Model.Data.Types.ModelType;

import java.util.Objects;

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
        return this.type.isCompatible(element.getDataType());
    }
    public boolean isCompatible (String element){
        return this.type.isCompatible(element);
    }

    public ModelType getDataType() {
        return type;
    }
/*
    public String getAlias() {
        return alias;
    }
    public boolean hasAlias() {
        return alias!=null;
    }*/

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DataElement that = (DataElement) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }
}
