package Persistence.Entities.Image.Operational;

import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class ConstraintEntity {

    private String constraintName;

    public ConstraintEntity () {}


    public ConstraintEntity (String name) {
        this.constraintName = name;
    }
    public String getName () {
        return constraintName;
    }

    public void setName (String name) {
        this.constraintName = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ConstraintEntity that = (ConstraintEntity) o;
        return Objects.equals(constraintName, that.constraintName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(constraintName);
    }
}
