package Persistence.Entities.Image.Data;

import Persistence.Entities.Image.ImageComponentKey;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
    private String alias;

    public SetEntity () {}

    public SetEntity (ImageComponentKey imageComponentKey, String type, List<String> data) {
        this.imageComponentKey = imageComponentKey;
        this.type = type;
        this.data = data;
        this.alias = null;
    }
    public SetEntity (UUID id, String name, String type, List<String> data) {
        this.imageComponentKey= new ImageComponentKey(id,name);
        this.type = type;
        this.data = data;
        this.alias = null;
    }
    public SetEntity (UUID id, String name, String type, List<String> data, String alias) {
        this.imageComponentKey= new ImageComponentKey(id,name);
        this.type = type;
        this.data = data;
        this.alias = alias;
    }
    public SetEntity (ImageComponentKey imageComponentKey, String type, List<String> data, String alias) {
        this.imageComponentKey = imageComponentKey;
        this.type = type;
        this.data = data;
        this.alias = alias;
    }

    public ImageComponentKey getModelDataKey() {
        return imageComponentKey;
    }

    public String getType() {
        return type;
    }

    public List<String> getData() {
        return data;
    }

    public String getAlias () {
        return alias;
    }
    public boolean hasAlias () {
        return alias != null;
    }
    public void setAlias (String alias) {
        this.alias = alias;
    }
    public String getName(){
        return imageComponentKey.getName();
    }
    public UUID getUUID(){
        return imageComponentKey.getImageId();
    }
    @Override
    public boolean equals(Object o) {
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
