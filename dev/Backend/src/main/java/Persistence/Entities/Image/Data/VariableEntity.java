package Persistence.Entities.Image.Data;

import Persistence.Entities.Image.ImageComponentKey;
import jakarta.persistence.*;
import org.jetbrains.annotations.Nullable;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Entity
@Table(name = "variables")
public class VariableEntity {

    @EmbeddedId
    @NonNull
    private ImageComponentKey imageComponentKey;

    @Column(name = "alias")
    @NonNull
    private String alias;


    @ElementCollection
    @CollectionTable(name = "variable_type_structure", joinColumns = {
            @JoinColumn(name = "image_id", referencedColumnName = "image_id"),
            @JoinColumn(name = "element_name", referencedColumnName = "element_name")
    })
    @AttributeOverrides({
            @AttributeOverride(name = "alias", column = @Column(name = "type_alias")),
            @AttributeOverride(name = "type", column = @Column(name = "type_name"))
    })
    private List<VariableTypeAliasPair> typeStructure;




    public VariableEntity() {}
    public VariableEntity(UUID id, @NonNull String varName, @Nullable String alias,@NonNull List<VariableTypeAliasPair> typeStructure) {
        this.imageComponentKey= new ImageComponentKey(id,varName);
        this.typeStructure = typeStructure;
        if(alias == null || alias.isBlank())
            this.alias = varName;
        else this.alias = alias;
    }

    public VariableEntity(UUID id, @NonNull String varName, @NonNull List<String> typeStructure, @Nullable String alias) {
        this.imageComponentKey= new ImageComponentKey(id,varName);
        this.typeStructure = typeStructure.stream().map(
                type -> new VariableTypeAliasPair(type, type)
        ).collect(Collectors.toList());
        if(alias == null || alias.isBlank())
            this.alias = varName;
        else this.alias = alias;
    }

    public VariableEntity(UUID id, @NonNull String varName, @NonNull List<String> typeStructure, @Nullable String alias, @Nullable List<String> typeAlias) {
        this.imageComponentKey= new ImageComponentKey(id,varName);
        if(typeAlias == null) {
            this.typeStructure = typeStructure.stream().map(
                    type -> new VariableTypeAliasPair(type, type))
                    .collect(Collectors.toList());
        }
        else if(typeStructure.size() != typeAlias.size())
            throw new IllegalArgumentException("Type alias and variable type length do not match");
        else this.typeStructure = IntStream.range(0, typeStructure.size())
                .mapToObj(i -> new VariableTypeAliasPair(
                        typeAlias.get(i),
                        typeStructure.get(i)
                ))
                .collect(Collectors.toList());

        if(alias == null || alias.isBlank())
            this.alias = varName;
        else this.alias = alias;
    }


    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VariableEntity that = (VariableEntity) o;
        return Objects.equals(imageComponentKey, that.imageComponentKey) && Objects.equals(alias, that.alias) && Objects.equals(typeStructure, that.typeStructure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageComponentKey, alias, typeStructure);
    }

    @NonNull
    public List<VariableTypeAliasPair> getTypeStructure() {
        return typeStructure;
    }

    public String getAlias() {
        if(alias == null || alias.isBlank())
            return getName();
        else return alias;
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
