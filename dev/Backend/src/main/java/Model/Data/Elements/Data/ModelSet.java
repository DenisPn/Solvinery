package Model.Data.Elements.Data;

import Model.Data.Types.ModelType;
import jakarta.persistence.*;

import java.util.List;
import java.util.SequencedCollection;


public class ModelSet extends DataElement {

    private final List<String> data;
    public ModelSet (String name, ModelType type, List<String> data) {
        super(name,type);
        this.data = data;
    }

    public ModelSet (String name, ModelType type, List<String> data,String alias) {
        super(name,type,alias);
        this.data = data;
    }

    public List<String> getData () {
        return data;
    }


    public boolean isEmpty () {
        return data.isEmpty();
    }
    public int size () {
        return data.size();
    }
    public boolean contains (String element) {
        return data.contains(element);
    }
}
