package Persistence.Entities.Image.Data;

import jakarta.persistence.Embeddable;

@Embeddable
public record VariableTypeAliasPair(String alias, String type) {
}
