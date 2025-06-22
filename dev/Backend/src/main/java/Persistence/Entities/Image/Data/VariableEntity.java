package Persistence.Entities.Image.Data;

import Persistence.Entities.Image.ImageComponentKey;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "variables")
public class VariableEntity {

    @EmbeddedId
    @NonNull
    private ImageComponentKey imageComponentKey;

    @Column(name = "alias")
    @NonNull
    private String alias;

    @Column(name = "ObjectiveValueAlias")
    @Nullable
    String objectiveValueAlias;

    @ElementCollection
    @CollectionTable(name = "variable_type_structure", joinColumns = {
            @JoinColumn(name = "image_id", referencedColumnName = "image_id"),
            @JoinColumn(name = "element_name", referencedColumnName = "element_name")
    })
    private List<String> typeStructure;




    public VariableEntity() {}

    public VariableEntity(UUID id, @NonNull String varName, @Nullable String alias, @NonNull List<String> typeStructure,@Nullable String objectiveValueAlias) {
        this.imageComponentKey= new ImageComponentKey(id,varName);
        this.typeStructure = typeStructure;
        if(alias == null || alias.isBlank())
            this.alias = varName;
        else this.alias = alias;
        this.objectiveValueAlias = Objects.requireNonNullElse(objectiveValueAlias, "Objective Value");
    }


    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VariableEntity that = (VariableEntity) o;
        return Objects.equals(imageComponentKey, that.imageComponentKey) && Objects.equals(alias, that.alias) && Objects.equals(typeStructure, that.typeStructure) && Objects.equals(objectiveValueAlias, that.objectiveValueAlias) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageComponentKey, alias, typeStructure,objectiveValueAlias);
    }

    @NonNull
    public List<String> getTypeStructure() {
        return typeStructure;
    }

    public String getAlias() {
        return Objects.requireNonNullElse(alias, imageComponentKey.getName());
    }

    @Nullable
    public String getObjectiveValueAlias() {
        return Objects.requireNonNullElse(objectiveValueAlias, "Objective Value");
    }

    public void setAlias(@NonNull String alias) {
        this.alias = alias;
    }
    public String getName(){
        return imageComponentKey.getName();
    }
    public UUID getUUID(){
        return imageComponentKey.getImageId();
    }

}
