package Persistence.Entities.Image.Data;

import Persistence.Entities.Image.ImageComponentKey;
import Persistence.Entities.Image.Operational.ConstraintEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "variables")
public class VariableEntity {

    @EmbeddedId
    private ImageComponentKey imageComponentKey;

    @Column(name = "alias")
    private String alias;

    public VariableEntity() {}

    public VariableEntity(UUID id,String varName, String alias) {
        this.imageComponentKey= new ImageComponentKey(id,varName);
        this.alias = alias;
}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariableEntity that = (VariableEntity) o;
        return imageComponentKey.equals(that.imageComponentKey) &&
                alias.equals(that.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageComponentKey, alias);
    }
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
    public String getName(){
        return imageComponentKey.getName();
    }
    public UUID getUUID(){
        return imageComponentKey.getImageId();
    }


}
