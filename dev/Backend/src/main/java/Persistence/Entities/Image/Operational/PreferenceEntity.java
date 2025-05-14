package Persistence.Entities.Image.Operational;

import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class PreferenceEntity {

    private String preferenceName;

    public PreferenceEntity () {}

    public PreferenceEntity (String name) {
        this.preferenceName = name;
    }
    public String getName () {
        return preferenceName;
    }

    public void setName (String name) {
        this.preferenceName = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreferenceEntity that = (PreferenceEntity) o;
        return preferenceName.equals(that.preferenceName);
    }
    @Override
    public int hashCode() {
        return Objects.hash(preferenceName);
    }
}
