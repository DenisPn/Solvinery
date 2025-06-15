package Persistence.Entities.Image.Data;

import Persistence.Entities.Image.ImageComponentKey;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name = "params")
public class ParameterEntity {

    @EmbeddedId
    private ImageComponentKey imageComponentKey;

    @Column(name = "structure", nullable = false)
    @NotBlank(message = "Structure cannot be blank")
    private String structure;

    @Column(name = "data", nullable = false)
    @NotNull(message = "Data cannot be null")
    private String data;


    @Column(name = "alias")
    private @NonNull String alias;


    public ParameterEntity () {}


    public ParameterEntity (ImageComponentKey imageComponentKey, String structure, @NonNull String data,@Nullable String alias) {
        this.imageComponentKey = imageComponentKey;
        this.structure = structure;
        this.data = data;
        if(alias == null || alias.isBlank())
            this.alias = imageComponentKey.getName();
        else this.alias = alias;
    }
    public ParameterEntity (UUID id, String name, String structure, @NonNull String data, @Nullable String alias) {
        this.imageComponentKey= new ImageComponentKey(id,name);
        this.structure = structure;
        this.data = data;
        if(alias == null || alias.isBlank())
            this.alias = imageComponentKey.getName();
        else this.alias = alias;
    }
    public ParameterEntity (ImageComponentKey imageComponentKey, String structure, @NonNull String data) {
        this.imageComponentKey = imageComponentKey;
        this.structure = structure;
        this.data = data;
        this.alias = imageComponentKey.getName();
    }
    public ImageComponentKey getModelDataKey() {
        return imageComponentKey;
    }

    public String getStructure() {
        return structure;
    }

    @NonNull
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
    public boolean equals(@Nullable Object o) {
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
