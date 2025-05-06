package Model.Data.Elements.Data;

import Model.Data.Types.ModelType;
import jakarta.persistence.*;

import java.util.List;
import java.util.SequencedCollection;


public class ModelSet extends DataElement {

    private List<String> data;
    private final boolean isPrimitive;
    public ModelSet (String name, ModelType type, List<String> data) {
        super(name,type);
        this.data = data;
        isPrimitive=true;
    }
    public ModelSet (String name, ModelType type) {
        super(name,type);
        this.data = null;
        isPrimitive=false;
    }

    public List<String> getData () {
        if(!isPrimitive)
            throw new IllegalStateException("Cannot get data from complex set");
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

    public void setData (List<String> data) {
        this.data = data;
    }

    public boolean isPrimitive() {
        return isPrimitive;
    }
}
