package Persistence.Entities.Image.Data;

import Persistence.Entities.Image.ImageComponentKey;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name = "params")
public class ParameterEntity {

    @EmbeddedId
    private ImageComponentKey imageComponentKey;

    @Column(name = "type", nullable = false)
    @NotBlank(message = "Type cannot be blank")
    private String type;

    @Column(name = "data", nullable = false)
    @NotNull(message = "Data cannot be null")
    private String data;


    @Column(name = "alias")
    private String alias;


    public ParameterEntity () {}

    public ParameterEntity (ImageComponentKey imageComponentKey, String type, String data) {
        this.imageComponentKey = imageComponentKey;
        this.type = type;
        this.data = data;
        this.alias = null;
    }
    public ParameterEntity (ImageComponentKey imageComponentKey, String type, String data, String alias) {
        this.imageComponentKey = imageComponentKey;
        this.type = type;
        this.data = data;
        this.alias = alias;
    }
    public ParameterEntity (UUID id, String name, String type, String data) {
        this.imageComponentKey= new ImageComponentKey(id,name);
        this.type = type;
        this.data = data;
        this.alias = null;
    }
    public ParameterEntity (UUID id, String name, String type, String data, String alias) {
        this.imageComponentKey= new ImageComponentKey(id,name);
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

    public String getData() {
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
        ParameterEntity that = (ParameterEntity) o;
        return imageComponentKey.equals(that.imageComponentKey) &&
                alias.equals(that.alias) &&
                data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageComponentKey, alias,data);
    }
}
