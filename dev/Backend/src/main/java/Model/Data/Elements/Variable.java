package Model.Data.Elements;

import java.util.List;
import java.util.Objects;
import java.util.SequencedCollection;

public class Variable extends Element {
    private final List<String> structure;
    //private final String alias;

   /* public Variable (String name, List<String> structure, String alias) {
        super(name);
        this.structure = structure;
        this.alias = alias;
    }*/
    public Variable (String name, List<String> structure) {
        super(name);
        this.structure = structure;
     //   this.alias = null;
    }
    public List<String> getStructure() {
        return structure;
    }

    /*public String getAlias () {
        return alias;
    }*/

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
