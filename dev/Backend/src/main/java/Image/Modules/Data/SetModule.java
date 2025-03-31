package Image.Modules.Data;

import Model.Data.Elements.Data.ModelSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetModule {
    private final Set<ModelSet> visibleSets;
    private final Map<String,String> setAliases;

    public SetModule(Set<ModelSet> visibleSets, Map<String, String> setAliases) {
        Set<String> visibleSetNames = visibleSets.stream()
                .map(ModelSet::getName)
                .collect(Collectors.toSet());
        if(!visibleSetNames.containsAll(setAliases.keySet())){
            throw new IllegalArgumentException("The set aliases do not contain all visible sets");
        }
        this.visibleSets = visibleSets;
        this.setAliases = setAliases;
    }
    public SetModule(Set<ModelSet> visibleSets) {
        this.visibleSets = visibleSets;
        this.setAliases = new HashMap<>();
    }
    public SetModule() {
        this.visibleSets = new HashSet<>();
        this.setAliases = new HashMap<>();
    }
    public void addAlias(ModelSet set, String alias){
        if(!visibleSets.contains(set)){
            throw new IllegalArgumentException("The set is not visible, its alias can't be changed");
        }
        setAliases.put(set.getName(),alias);
    }
    public void removeAlias(ModelSet set){
        if(!visibleSets.contains(set)){
            throw new IllegalArgumentException("The set is not visible, its alias can't be changed");
        }
        setAliases.remove(set.getName());
    }
    public void makeInvisible(ModelSet set){
        visibleSets.remove(set);
        setAliases.remove(set.getName());
    }
    public void makeVisible(ModelSet set){
        visibleSets.add(set);
    }
    public Set<ModelSet> getVisibleSets() {
        return visibleSets;
    }
    public Map<String, String> getSetAliases() {
        return setAliases;
    }
    public Stream<ModelSet> getVisibleSetsAsStream(){
        return visibleSets.stream();
    }
    public Stream<Map.Entry<String,String>> getSetAliasesAsStream(){
        return setAliases.entrySet().stream();
    }
    public Map<String,String> getSetAliasesAsMap(){
        return new HashMap<>(setAliases);
    }
}
