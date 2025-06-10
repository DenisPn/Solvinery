package Persistence.Entities.Image.Data;

import Persistence.Entities.Image.ImageComponentKey;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @Column(name = "type", nullable = false)
    @NotBlank(message = "Type cannot be blank")
    private String type;

    @ElementCollection
    @CollectionTable(name = "set_data", joinColumns = {
            @JoinColumn(name = "image_id",
            referencedColumnName = "image_id"),
            @JoinColumn(name = "element_name",
            referencedColumnName = "element_name")
        }
    )
    @Column(name = "data_element")
    @NotNull(message = "Data cannot be null")
    private List<String> data;

    @Column(name = "alias")
    private @NonNull String alias;

    @Column(name = "type_alias")
    private String typeAlias;

    public SetEntity () {}

    public SetEntity (@NotNull ImageComponentKey imageComponentKey,@NotNull String type, @NotNull List<String> data) {
        this.imageComponentKey = imageComponentKey;
        this.type = type;
        this.data = data;
        this.alias = imageComponentKey.getName();
        this.typeAlias = type;
    }
    public SetEntity (@NotNull UUID id,@NotNull String name,@NotNull String type, @NotNull List<String> data) {
        this.imageComponentKey= new ImageComponentKey(id,name);
        this.type = type;
        this.data = data;
        this.alias = name;
        this.typeAlias = type;
    }
    public SetEntity (@NotNull UUID id,@NotNull String name,@NotNull String type, @NotNull List<String> data,@Nullable String alias) {
        this.imageComponentKey= new ImageComponentKey(id,name);
        this.type = type;
        this.data = data;
        this.alias = (alias == null) ? name : alias;
        this.typeAlias = type;
    }
    public SetEntity (@NotNull ImageComponentKey imageComponentKey,@NotNull String type, @NotNull List<String> data,@Nullable String alias) {
        this.imageComponentKey = imageComponentKey;
        this.type = type;
        this.data = data;
        this.alias = (alias == null) ? imageComponentKey.getName() : alias;
        this.typeAlias = type;
    }
    public SetEntity (@NotNull UUID id,@NotNull String name,@NotNull String type, @NotNull List<String> data,@NotNull String alias,@NotNull String typeAlias) {
        this.imageComponentKey= new ImageComponentKey(id,name);
        this.type = type;
        this.data = data;
        this.alias = alias;
        this.typeAlias = typeAlias;
    }
    public ImageComponentKey getModelDataKey() {
        return imageComponentKey;
    }

    public String getType() {
        return type;
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

    public String getTypeAlias() {
        return typeAlias;
    }

    public void setTypeAlias(String typeAlias) {
        this.typeAlias = typeAlias;
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
