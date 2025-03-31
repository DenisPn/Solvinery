package Image.Modules;

import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Preference;

import java.util.*;

public class PreferenceModule extends Module{
    /**
     * a preference module, holding the user definition for a group of model preference
     * (a preference is a single expressions in the expression sum in the minimize/maximize expression in zimpl)
     */
    private final Map<String, Preference> preferences;




    public PreferenceModule(String name, String description) {
        super(name, description);
        preferences = new HashMap<>();

    }
    public PreferenceModule(String name, String description, Collection<Preference> preferences, Collection<String> inputSets, Collection<String> inputParams) {
        super(name, description,inputSets,inputParams);
        this.preferences = new HashMap<>();
        for (Preference constraint : preferences) {
            this.preferences.put(constraint.getName(), constraint);
        }

    }
    /**
     * Fetch all ModelSets that are in use in any of the preferences in the module.
     * @return all sets that are part of any preferences in the module
     */
    @Override
    public Set<ModelSet> getInvolvedSets(){
        HashSet<ModelSet> involvedSets = new HashSet<>();
        /*for(Preference constraint : preferences.values()){
            constraint.getPrimitiveSets(involvedSets);
        }*/
        return involvedSets;
    }

    @Override
    public Set<ModelParameter> getInvolvedParameters() {
        HashSet<ModelParameter> involvedParameters = new HashSet<>();
        /*for(Preference preference : preferences.values()){
          preference.getPrimitiveParameters(involvedParameters);
        }*/
        return involvedParameters;
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
