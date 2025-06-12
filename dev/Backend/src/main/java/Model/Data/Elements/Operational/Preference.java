package Model.Data.Elements.Operational;

import org.jetbrains.annotations.Nullable;
import org.springframework.lang.NonNull;

import java.util.Objects;

public class Preference extends OperationalElement {

    public Preference (String body) {
        super(body);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return super.equals(o); //scalar isn't checked
    }
    @NonNull
    @Override
    public ElementType getType() {
        return ElementType.PREFERENCE;
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
