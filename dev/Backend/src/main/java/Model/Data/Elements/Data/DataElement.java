package Model.Data.Elements.Data;

import Model.Data.Elements.Element;
import Model.ModelType;

public abstract class DataElement extends Element {
    private final ModelType type;
    public DataElement (String name, ModelType type) {
        super(name);
        this.type = type;
    }
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
