package Model.Data.Elements.Data;

import Model.ModelType;

import java.util.List;
import java.util.SequencedCollection;

public class ModelSet extends DataElement {
    private SequencedCollection<String> data;
    public ModelSet (String name, ModelType type, SequencedCollection<String> data) {
        super(name,type);
        this.data = data;
    }

    public SequencedCollection<String> getData () {
        return data;
    }
    public boolean isEmpty () {
        return data.isEmpty();
    }

}
