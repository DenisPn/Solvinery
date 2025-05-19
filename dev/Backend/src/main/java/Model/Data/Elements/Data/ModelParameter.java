package Model.Data.Elements.Data;

import Model.Data.Types.ModelType;

import java.util.Objects;

public class ModelParameter extends DataElement {
    private String data;
    private final boolean auxiliary;
    public ModelParameter (String name, ModelType type, String data) {
        super(name, type);
        this.data = data;
        this.auxiliary =false;
    }
    public ModelParameter (String name, ModelType type, String data, boolean auxiliary) {
        super(name, type);
        this.data = data;
        this.auxiliary = auxiliary;
    }

    public boolean isAuxiliary() {
        return auxiliary;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ModelParameter that = (ModelParameter) o;
        return auxiliary == that.auxiliary && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), data, auxiliary);
    }
    @Override
    public ElementType getType() {
        return ElementType.MODEL_PARAMETER;
    }
    public String getData () {
        return data;
    }

    public void setData (String data) {
        this.data = data;
    }
}
