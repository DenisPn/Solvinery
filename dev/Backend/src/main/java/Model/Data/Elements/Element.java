package Model.Data.Elements;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;

public abstract class Element {

    protected String name;

    public Element (String name) {
        this.name = name;
    }
    protected Element () {}
    public String getName () {
        return name;
    }
}
