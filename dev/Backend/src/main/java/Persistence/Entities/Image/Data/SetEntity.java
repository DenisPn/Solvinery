package Persistence.Entities.Image.Data;

import Persistence.Entities.Image.ImageComponentKey;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "sets")
public class SetEntity {

    @EmbeddedId
    private ImageComponentKey imageComponentKey;

    /*@Column(name = "structure", nullable = false)
    @NotBlank(message = "Type cannot be blank")
    private String structure;*/

    @ElementCollection
    @CollectionTable(name = "set_data", joinColumns = {
            @JoinColumn(name = "image_id",
            referencedColumnName = "image_id"),
            @JoinColumn(name = "element_name",
            referencedColumnName = "element_name")
        }
    )
    @Column(name = "data_element", nullable = false)
    @NotNull(message = "Data cannot be null")
    private List<String> data;

    @Column(name = "alias")
    private @NonNull String alias;

    @Column(name = "structure")
    private String structure;

    public SetEntity () {}


    public SetEntity (@NotNull ImageComponentKey imageComponentKey,@NotNull String structure, @NotNull List<String> data) {
        this.imageComponentKey = imageComponentKey;
        this.data = data;
        this.alias = imageComponentKey.getName();
        this.structure = structure;
    }
    public SetEntity (@NotNull UUID id,  @NotNull String name, @NotNull String structure, @NotNull List<String> data) {
        this.imageComponentKey= new ImageComponentKey(id,name);
        this.data = data;
        this.alias = name;
        this.structure = structure;
    }

    public SetEntity (@NotNull UUID id,@NotNull String name,@NotNull String structure, @NotNull List<String> data,@Nullable String alias) {
        this.imageComponentKey= new ImageComponentKey(id,name);
        this.data = data;
        this.alias = (alias == null) ? name : alias;
        this.structure = structure;
    }
    public SetEntity (@NotNull ImageComponentKey imageComponentKey,@NotNull String structure, @NotNull List<String> data,@Nullable String alias) {
        this.imageComponentKey = imageComponentKey;
        this.data = data;
        this.alias = (alias == null) ? imageComponentKey.getName() : alias;
        this.structure = structure;
    }

    public ImageComponentKey getModelDataKey() {
        return imageComponentKey;
    }
    @NonNull
    public String getStructure() {
        return structure;
    }

    @NonNull
    public List<String> getData() {
        return data;
    }

    @NonNull
    public String getAlias () {
        return alias;
    }
    public boolean hasAlias () {
        return alias != null;
    }
    public void setAlias (@NonNull String alias) {
        this.alias = alias;
    }
    public String getName(){
        return imageComponentKey.getName();
    }
    public UUID getUUID(){
        return imageComponentKey.getImageId();
    }


    public void setStructure(String typeAlias) {
        this.structure = typeAlias;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetEntity that = (SetEntity) o;
        return imageComponentKey.equals(that.imageComponentKey) &&
                alias.equals(that.alias) &&
                data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageComponentKey, alias, data);
    }
}
