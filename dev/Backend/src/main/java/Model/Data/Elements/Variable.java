package Model.Data.Elements;

import java.util.List;
import java.util.Objects;

public class Variable extends Element {
    private final List<String> structure;
    private final List<String> basicSets;

    public Variable (String name, List<String> structure,List<String> basicSets) {
        super(name);
        this.structure = structure;
        this.basicSets = basicSets;
    }
    @Deprecated(forRemoval = true)
    public Variable (String name, List<String> structure) {
        super(name);
        this.structure = structure;
        this.basicSets = null;
    }

    public List<String> getBasicSets() {
        return basicSets;
    }

    public List<String> getStructure() {
        return structure;
    }



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Variable variable = (Variable) o;
        return Objects.equals(structure, variable.structure);
    }

    @Override
    public ElementType getType() {
        return ElementType.VARIABLE;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), structure);
    }
}
