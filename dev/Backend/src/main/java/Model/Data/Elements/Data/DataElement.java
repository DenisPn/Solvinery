package Model.Data.Elements.Data;

import Model.Data.Elements.Element;
import Model.Data.Types.ModelType;
import jakarta.persistence.MappedSuperclass;

public abstract class DataElement extends Element {
    private ModelType type;
    public DataElement (String name, ModelType type) {
        super(name);
        this.type = type;
    }
    protected DataElement () {}
    public boolean isCompatible (DataElement element){
        return this.type.isCompatible(element.getType());
    }
    public boolean isCompatible (String element){
        return this.type.isCompatible(element);
    }

    public ModelType getType () {
        return type;
    }
}
