package Model.Data.Elements;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class Element {

    protected final String name;

    public Element (String name) {
        this.name = name;
    }
    public String getName () {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Element element = (Element) o;
        return Objects.equals(name, element.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
    public abstract ElementType getType();
    public enum ElementType {
        MODEL_SET,
        MODEL_PARAMETER,
        CONSTRAINT,
        PREFERENCE,
        VARIABLE
    }

}
