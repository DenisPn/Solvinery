package Model.Data.Elements;

import java.util.List;
import java.util.SequencedCollection;

public class Variable extends Element {
    private final List<String> structure;

    public Variable (String name, List<String> structure) {
        super(name);
        this.structure = structure;
    }
    public List<String> getStructure() {
        return structure;
    }
}
