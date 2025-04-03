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
@Table(name = "preference_module")
public class PreferenceModuleEntity {
    @EmbeddedId
    private ImageComponentKey imageComponentKey;

    @Column(name = "description", nullable = false)
    @NotNull(message = "Description cannot be null, if no description exists it should be blank")
    private String description;

    @ElementCollection
    @CollectionTable(name = "preferences", joinColumns = {
            @JoinColumn(name = "image_id",
                    referencedColumnName = "image_id"),
            @JoinColumn(name = "element_name",
                    referencedColumnName = "element_name")
    }
    )
    @Column(name = "preference", nullable = false)
    @NotNull(message = "Preferences list cannot be null")
    @Valid
    private Set<@NotNull PreferenceEntity> preferences;
    public PreferenceModuleEntity () {}

    public PreferenceModuleEntity (ImageComponentKey imageComponentKey, String description, Set<PreferenceEntity> preferences) {
        this.imageComponentKey = imageComponentKey;
        this.description = description;
        this.preferences = preferences;
    }
    public PreferenceModuleEntity (ImageComponentKey imageComponentKey, String description) {
        this.imageComponentKey = imageComponentKey;
        this.description = description;
        this.preferences = new HashSet<>();
    }
    public PreferenceModuleEntity (UUID id,String name, String description, Set<PreferenceEntity> preferences) {
        this.imageComponentKey= new ImageComponentKey(id,name);
        this.description = description;
        this.preferences = preferences;
    }
    public PreferenceModuleEntity (UUID id,String name, String description) {
        this.imageComponentKey= new ImageComponentKey(id,name);
        this.description = description;
        this.preferences = new HashSet<>();
    }
    public String getName(){
        return imageComponentKey.getName();
    }
    public UUID getUUID(){
        return imageComponentKey.getImageId();
    }

    public ImageComponentKey getImageComponentKey () {
        return imageComponentKey;
    }

    public String getDescription () {
        return description;
    }

    public Set<PreferenceEntity> getPreferences () {
        return preferences;
    }

    public void setImageComponentKey (ImageComponentKey imageComponentKey) {
        this.imageComponentKey = imageComponentKey;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public void setPreferences (Set<PreferenceEntity> preferences) {
        this.preferences = preferences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreferenceModuleEntity that = (PreferenceModuleEntity) o;
        return imageComponentKey.equals(that.imageComponentKey) &&
                preferences.equals(that.preferences)
                && description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageComponentKey, description, preferences);
    }
}
