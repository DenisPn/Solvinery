package Persistence.Entities.Model.Data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Entity
@Table(name = "params")
public class ModelParamEntity {

    @EmbeddedId
    private ModelDataKeyPair modelDataKeyPair;

    @Column(name = "type", nullable = false)
    @NotBlank(message = "Type cannot be blank")
    private String type;

    @Column(name = "data", nullable = false)
    @NotNull(message = "Data cannot be null")
    private String data;

    public ModelParamEntity () {}

    public ModelParamEntity (ModelDataKeyPair modelDataKeyPair, String type, String data) {
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

    public String getData() {
        return data;
    }
}
