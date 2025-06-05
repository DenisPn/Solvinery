package Model.Data.Elements.Data;

import Model.Data.Types.ModelType;

import java.util.List;
import java.util.Objects;


public class ModelSet extends DataElement {

    private List<String> data;
    private final boolean isPrimitive;
    public ModelSet (String name, ModelType type, List<String> data) {
        super(name,type);
        this.data = data;
        isPrimitive=true;
    }

    public ModelSet (String name, ModelType type, boolean isPrimitive) {
        super(name,type);
        this.data = null;
        this.isPrimitive=isPrimitive;
    }

    public List<String> getData () {
        if(!isPrimitive)
            throw new IllegalStateException("Cannot get data from complex set");
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ModelSet modelSet = (ModelSet) o;
        return isPrimitive == modelSet.isPrimitive && Objects.equals(data, modelSet.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), data, isPrimitive);
    }
    @Override
    public ElementType getType() {
        return ElementType.MODEL_SET;
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
