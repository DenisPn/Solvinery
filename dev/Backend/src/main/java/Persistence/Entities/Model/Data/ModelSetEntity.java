package Persistence.Entities.Model.Data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
@Table(name = "sets")
public class ModelSetEntity {

    @EmbeddedId
    private ModelDataKeyPair modelDataKeyPair;
    @Column(name = "type", nullable = false)
    @NotBlank(message = "Type cannot be blank")
    private String type;

    @ElementCollection
    @CollectionTable(name = "set_data", joinColumns = {
            @JoinColumn(name = "image_id",
            referencedColumnName = "image_id"),
            @JoinColumn(name = "name",
            referencedColumnName = "name")
        }
    )
    @Column(name = "element")
    @NotNull(message = "Data cannot be null")
    private List<String> data;

    @Column(name = "alias")
    private String alias;

    public ModelSetEntity() {}

    public ModelSetEntity(ModelDataKeyPair modelDataKeyPair, String type, List<String> data) {
        this.modelDataKeyPair = modelDataKeyPair;
        this.type = type;
        this.data = data;
        this.alias = null;
    }
    public ModelSetEntity(ModelDataKeyPair modelDataKeyPair, String type, List<String> data, String alias) {
        this.modelDataKeyPair = modelDataKeyPair;
        this.type = type;
        this.data = data;
        this.alias = alias;
    }

    public ModelDataKeyPair getModelDataKey() {
        return modelDataKeyPair;
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
}
