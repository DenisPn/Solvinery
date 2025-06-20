package Persistence.Entities.Image.Operational;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Embeddable
public class PreferenceEntity {
    @Column(length = 4000)
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
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PreferenceEntity that = (PreferenceEntity) o;
        return Objects.equals(preferenceName, that.preferenceName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(preferenceName);
    }
}
