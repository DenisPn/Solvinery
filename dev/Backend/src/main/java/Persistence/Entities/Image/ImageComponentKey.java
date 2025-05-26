package Persistence.Entities.Image;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A pair of primary keys, one is the ImageId of the image the entity using this key belongs to,
 * the other is the name of the entity.
 * Two key pairs are equal when both imageIds and names are equal.
 *
 * <p>Column names:</p>
 * <ul>
 *     <li><b>imageId</b>: {@code "image_id"}</li>
 *     <li><b>name</b>: {@code "name"}</li>
 * </ul>
 */
@Embeddable
public class ImageComponentKey implements Serializable {
    @Column(name = "image_id", nullable = false)
    private UUID imageId;

    @Column(name = "element_name", nullable = false)
    private String name;

    public ImageComponentKey () {}

    public ImageComponentKey (UUID imageId, String name) {
        this.imageId = imageId;
        this.name = name;
    }

    public UUID getImageId() {
        return imageId;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Override equals() and hashCode() for composite key logic
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageComponentKey that = (ImageComponentKey) o;
        return imageId.equals(that.imageId) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageId, name);
    }
}
