package Image.Modules.Grouping;

import Model.Data.Elements.Operational.Preference;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.jetbrains.annotations.Nullable;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
/**
 * a preference module, holding the user definition for a group of model preference
 * (a preference is a single expressions in the expression sum in the minimize/maximize expression in zimpl)
 */
public class PreferenceModule extends OperationalModule {
    private static final float DEFAULT_SCALAR = 0.5F;
    @NonNull
    private final Map<String, Preference> preferences;

    private float scalar;



    public PreferenceModule(@NonNull String name, @NonNull String description) {
        super(name, description);
        preferences = new HashMap<>();
        this.scalar=DEFAULT_SCALAR;
    }
    public PreferenceModule(@NonNull String name, @NonNull String description, @NonNull Collection<Preference> preferences, @Nullable Float scalar) {
        super(name, description);
        this.preferences = new HashMap<>();
        for (Preference preference : preferences) {
            this.preferences.put(preference.getName(), preference);
        }
        if(scalar==null) this.scalar=DEFAULT_SCALAR;
        else this.scalar=scalar;

    }
    public PreferenceModule(@NonNull String name, @NonNull String description, @NonNull Collection<Preference> preferences) {
        super(name, description);
        this.preferences = new HashMap<>();
        for (Preference constraint : preferences) {
            this.preferences.put(constraint.getName(), constraint);
        }
        this.scalar=DEFAULT_SCALAR;
    }
    public void setScalar(float scalar) {
        this.scalar = scalar;
    }

    public float getScalar() {
        return scalar;
    }

    public Preference getPreference(String name){
        return preferences.get(name);
    }
    @NonNull
    public Map<String, Preference> getPreferences() {
        return preferences;
    }
    public void addPreference(@NonNull Preference preference){
        preferences.put(preference.getName(),preference);
    }
    public void removePreference(@NonNull Preference preference){
        preferences.remove(preference.getName());
    }
    public void removePreference(String identifier){
        preferences.remove(identifier);
    }
}
