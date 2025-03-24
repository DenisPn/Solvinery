package Model.Data.Elements.Data;

import Model.Data.Elements.Element;
import Model.ModelType;

public abstract class DataElement extends Element {
    private final String type;

    public DataElement (String name, String type) {
        super(name);
        this.type = type;
    }

}
