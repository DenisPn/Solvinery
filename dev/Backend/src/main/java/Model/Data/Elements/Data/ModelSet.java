package Model.Data.Elements.Data;

import Model.Data.Types.ModelType;
import jakarta.persistence.*;

import java.util.List;
import java.util.SequencedCollection;


public class ModelSet extends DataElement {

    private List<String> data;
    public ModelSet (String name, ModelType type, List<String> data) {
        super(name,type);
        this.data = data;
    }

    protected ModelSet() {
        super();
    }

    public List<String> getData () {
        return data;
    }
    public boolean isEmpty () {
        return data.isEmpty();
    }

}
