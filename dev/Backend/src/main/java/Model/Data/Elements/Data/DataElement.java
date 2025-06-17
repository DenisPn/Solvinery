package Model.Data.Elements.Data;

import Model.Data.Elements.Element;
import Model.Data.Types.ModelType;
import org.jetbrains.annotations.Nullable;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;

public abstract class DataElement extends Element {
    protected final ModelType type;


    public DataElement (String name, ModelType type) {
        super(name);
        this.type = type;
    }

   public boolean isCompatible(List<String> content){
       return content.stream().allMatch(type::isCompatible);
   }
    public boolean isCompatible (@NonNull DataElement element){
        return this.type.isCompatible(element.getDataType());
    }
    public boolean isCompatible (String element){
        return this.type.isCompatible(element);
    }

    public ModelType getDataType() {
        return type;
    }


    @Override
    public boolean equals(@Nullable Object o) {
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
