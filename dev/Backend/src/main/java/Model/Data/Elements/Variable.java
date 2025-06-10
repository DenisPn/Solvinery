package Model.Data.Elements;

import org.jetbrains.annotations.Nullable;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;

public class Variable extends Element {
    private final List<String> typeStructure;
    @Deprecated(forRemoval = true)
    private List<String> basicSets;
    @Deprecated(forRemoval = true)
    public Variable (String name, List<String> typeStructure, List<String> basicSets) {
        super(name);
        this.typeStructure = typeStructure;
        this.basicSets = basicSets;
    }
    public Variable (String name, List<String> typeStructure) {
        super(name);
        this.typeStructure = typeStructure;
    }

    public List<String> getTypeStructure() {
        return typeStructure;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Variable variable = (Variable) o;
        return Objects.equals(typeStructure, variable.typeStructure);
    }

    @NonNull
    @Override
    public ElementType getType() {
        return ElementType.VARIABLE;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), typeStructure);
    }
}
