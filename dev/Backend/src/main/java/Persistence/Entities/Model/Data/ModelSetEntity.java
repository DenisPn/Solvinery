package Persistence.Entities.Model.Data;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "sets")
public class ModelSetEntity {
    @EmbeddedId
    private ModelDataKeyPair modelDataKeyPair;
    @Column(name = "type", nullable = false)
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
    private List<String> data;

    public ModelSetEntity() {}

    public ModelSetEntity(ModelDataKeyPair modelDataKeyPair, String type, List<String> data) {
        this.modelDataKeyPair = modelDataKeyPair;
        this.type = type;
        this.data = data;
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
}
