package Persistence.Entities.Image.Data;

import Persistence.Entities.Image.ImageComponentKey;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "variables")
public class VariableEntity {

    @EmbeddedId
    private ImageComponentKey imageComponentKey;

    @Column(name = "alias")
    private String alias;


    @ElementCollection
    @CollectionTable(name = "structure_parts", joinColumns = {
            @JoinColumn(name = "image_id",
                    referencedColumnName = "image_id"),
            @JoinColumn(name = "element_name",
                    referencedColumnName = "element_name")
    }
    )
    @Column(name = "structure_part")
    @NotNull(message = "Structure can't be null.")
    private List<String> structure;
    public VariableEntity() {}

    public VariableEntity(UUID id,String varName, List<String> structure,String alias) {
        this.imageComponentKey= new ImageComponentKey(id,varName);
        this.structure = structure;
        this.alias = alias;
}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VariableEntity that = (VariableEntity) o;
        return Objects.equals(imageComponentKey, that.imageComponentKey) && Objects.equals(alias, that.alias) && Objects.equals(structure, that.structure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageComponentKey, alias, structure);
    }

    public List<String> getStructure () {
        return structure;
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
