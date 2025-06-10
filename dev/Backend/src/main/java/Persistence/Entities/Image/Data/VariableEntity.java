package Persistence.Entities.Image.Data;

import Persistence.Entities.Image.ImageComponentKey;
import jakarta.persistence.*;
import org.jetbrains.annotations.Nullable;

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
    //@NotNull(message = "Structure can't be null.")
    private List<String> structure;


    @ElementCollection
    @CollectionTable(name = "variable_set_structure", joinColumns = {
            @JoinColumn(name = "image_id", referencedColumnName = "image_id"),
            @JoinColumn(name = "element_name", referencedColumnName = "element_name")
    })
    @Column(name = "set_name")
    private List<String> setStructure;

    public VariableEntity() {}

    @Deprecated(forRemoval = true)
    public VariableEntity(UUID id,String varName, List<String> structure,String alias) {
        this.imageComponentKey= new ImageComponentKey(id,varName);
        this.structure = structure;
        if(alias == null || alias.isBlank())
            this.alias = varName;
        else this.alias = alias;
}

    public VariableEntity(UUID id,String varName, List<String> structure,List<String> setStructure,String alias) {
        this.imageComponentKey= new ImageComponentKey(id,varName);
        this.structure = structure;
        this.setStructure = setStructure;
        if(alias == null || alias.isBlank())
            this.alias = varName;
        else this.alias = alias;
    }


    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VariableEntity that = (VariableEntity) o;
        return Objects.equals(imageComponentKey, that.imageComponentKey) && Objects.equals(alias, that.alias) && Objects.equals(structure, that.structure) && Objects.equals(setStructure, that.setStructure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageComponentKey, alias, structure, setStructure);
    }

    public List<String> getStructure () {
        return structure;
    }
    public String getAlias() {
        if(alias == null || alias.isBlank())
            return getName();
        else return alias;
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

    public List<String> getSetStructure() {
        return setStructure;
    }

    public void setSetStructure(List<String> setStructure) {
        this.setStructure = setStructure;
    }

    public void setStructure(List<String> structure) {
        this.structure = structure;
    }
}
