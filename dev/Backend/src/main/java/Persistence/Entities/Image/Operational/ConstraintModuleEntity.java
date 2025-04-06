package Persistence.Entities.Image.Operational;

import Persistence.Entities.Image.ImageComponentKey;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "constraint_module")
public class ConstraintModuleEntity {
    @EmbeddedId
    private ImageComponentKey imageComponentKey;

    @Column(name = "description", nullable = false)
    @NotNull(message = "Description cannot be null, if no description exists it should be blank")
    private String description;

    @ElementCollection
    @CollectionTable(name = "constraints", joinColumns = {
            @JoinColumn(name = "image_id",
                    referencedColumnName = "image_id"),
            @JoinColumn(name = "element_name",
                    referencedColumnName = "element_name")
    }
    )
    @Column(name = "constraint", nullable = false)
    @NotNull(message = "Constraint list cannot be null")
    private @Valid Set<@NotNull ConstraintEntity> constraints;

    public ConstraintModuleEntity () {}

    public ConstraintModuleEntity (ImageComponentKey imageComponentKey, String description, Set<ConstraintEntity> constraints) {
        this.imageComponentKey = imageComponentKey;
        this.description = description;
        this.constraints = constraints;
    }
    public ConstraintModuleEntity (ImageComponentKey imageComponentKey, String description) {
        this.imageComponentKey = imageComponentKey;
        this.description = description;
        this.constraints = new HashSet<>();
    }
    public ConstraintModuleEntity (UUID id,String name, String description, Set<ConstraintEntity> constraints) {
        this.imageComponentKey= new ImageComponentKey(id,name);
        this.description = description;
        this.constraints = constraints;
    }
    public ConstraintModuleEntity (UUID id,String name, String description) {
        this.imageComponentKey= new ImageComponentKey(id,name);
        this.description = description;
        this.constraints = new HashSet<>();
    }

    public ImageComponentKey getImageComponentKey () {
        return imageComponentKey;
    }

    public String getDescription () {
        return description;
    }

    public Set<ConstraintEntity> getConstraints () {
        return constraints;
    }

    public void setImageComponentKey (ImageComponentKey imageComponentKey) {
        this.imageComponentKey = imageComponentKey;
    }

    public void setDescription (String description) {
        this.description = description;
    }
    public void addConstraint (ConstraintEntity constraint) {
        this.constraints.add(constraint);
    }
    public void removeConstraint (ConstraintEntity constraint) {
        this.constraints.remove(constraint);
    }
    public void removeAllConstraints () {
        this.constraints.clear();
    }
    public boolean isEmpty () {
        return !constraints.isEmpty();
    }
    public boolean hasConstraint (ConstraintEntity constraint) {
        return constraints.contains(constraint);
    }
    public void setConstraints (Set<ConstraintEntity> constraints) {
        this.constraints = constraints;
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
        ConstraintModuleEntity that = (ConstraintModuleEntity) o;
        return imageComponentKey.equals(that.imageComponentKey) &&
                constraints.equals(that.constraints)
                && description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageComponentKey, description, constraints);
    }

}
