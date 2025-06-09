package Image.Modules.Grouping;

import Model.Data.Elements.Operational.Preference;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PreferenceModule extends OperationalModule {
    /**
     * a preference module, holding the user definition for a group of model preference
     * (a preference is a single expressions in the expression sum in the minimize/maximize expression in zimpl)
     */
    private final Map<String, Preference> preferences;
    private float scalar;



    public PreferenceModule(String name, String description) {
        super(name, description);
        preferences = new HashMap<>();
        this.scalar=1F;
    }
    public PreferenceModule(String name, String description, Collection<Preference> preferences, Float scalar) {
        super(name, description);
        this.preferences = new HashMap<>();
        for (Preference constraint : preferences) {
            this.preferences.put(constraint.getName(), constraint);
        }
        if(scalar==null) this.scalar=1F;
        else this.scalar=scalar;

    }
    public PreferenceModule(String name, String description, Collection<Preference> preferences) {
        super(name, description);
        this.preferences = new HashMap<>();
        for (Preference constraint : preferences) {
            this.preferences.put(constraint.getName(), constraint);
        }

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
    public Map<String, Preference> getPreferences() {
        return preferences;
    }
    public void addPreference(Preference preference){
        preferences.put(preference.getName(),preference);
    }
    public void removePreference(Preference preference){
        preferences.remove(preference.getName());
    }
    public void removePreference(String identifier){
        preferences.remove(identifier);
    }
}
