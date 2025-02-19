package Image.Modules;

import Model.ModelInput;
import Model.ModelParameter;
import Model.ModelSet;
import Model.ModelVariable;

import java.util.*;

public class VariableModule {

    final Map<String, ModelVariable> variables;
    final Set<String> inputSets;
    final Set<String> inputParams;
    final Map<String, List<String>> aliases;

    public Map<String, ModelVariable> getVariables() {
        return variables;
    }

    public Set<String> getInputSets() {
        return inputSets;
    }

    public Set<String> getInputParams() {
        return inputParams;
    }

    public VariableModule() {
        variables = new HashMap<>();
        inputSets = new HashSet<>();
        inputParams = new HashSet<>();
        aliases = new HashMap<>();
    }

    public VariableModule(Map<String, ModelVariable> variables, Collection<String> inputSets, Collection<String> inputParams,Map<String,List<String>> aliases) {
        this.variables = new HashMap<>(variables);
        this.inputSets = new HashSet<>(inputSets);
        this.inputParams = new HashSet<>(inputParams);
        this.aliases = new HashMap<>(aliases);
        for (String variableName : variables.keySet()) {
            if(aliases.containsKey(variableName)) {
                this.aliases.put(variableName, aliases.get(variableName));
            }
            else{
                //if no specific alies is specified, alies will default to set names that make up the set
                this.aliases.put(variableName, getDefaultAliases(variables.get(variableName)));
            }
        }
    }
    public VariableModule(Set<ModelVariable> variables, Collection<String> inputSets, Collection<String> inputParams,Map<String,List<String>> aliases) {
        this.variables = new HashMap<>();
        for(ModelVariable variable: variables) {
            this.variables.put(variable.getIdentifier(),variable);
        }
        this.inputSets = new HashSet<>(inputSets);
        this.inputParams = new HashSet<>(inputParams);
        this.aliases = new HashMap<>(aliases);
        for (ModelVariable variable : variables) {
            String variableName = variable.getIdentifier();
            if(aliases.containsKey(variableName)) {
                this.aliases.put(variableName, aliases.get(variableName));
            }
            else{
                //if no specific alies is specified, alies will default to set names that make up the set
                this.aliases.put(variableName, getDefaultAliases(variable));
            }
        }
    }

    public void clear() {
        variables.clear();
        inputSets.clear();
        inputParams.clear();
    }

    public void override(Map<String, ModelVariable> variables, Collection<String> inputSets, Collection<String> inputParams,Map<String,List<String>> aliases) {
        this.variables.clear();
        this.inputSets.clear();
        this.inputParams.clear();
        this.variables.putAll(variables);
        this.inputSets.addAll(inputSets);
        this.inputParams.addAll(inputParams);
        for (String variableName : variables.keySet()) {
            if(aliases.containsKey(variableName)) {
                this.aliases.put(variableName, aliases.get(variableName));
            }
            else{
                this.aliases.put(variableName, getDefaultAliases(variables.get(variableName)));
            }
        }
    }
    public Set<String> getIdentifiers() {
        return variables.keySet();
    }
    public ModelVariable get(String name) {
        return variables.get(name);
    }

    public void addVariable(ModelVariable variable) {
        variables.put(variable.getIdentifier(),variable);
        if(!aliases.containsKey(variable.getIdentifier())) {
            aliases.put(variable.getIdentifier(), getDefaultAliases(variable));
        }
    }
    private List<String> getDefaultAliases(ModelVariable variable) {
        ArrayList<String> defaultAliases = new ArrayList<>();
        for (ModelSet modelSet : variable.getSetDependencies()) {
            for (ModelInput.StructureBlock block : modelSet.getStructure()) {
                defaultAliases.add(block.dependency.getIdentifier());
            }
        }
        if (defaultAliases.isEmpty())
            defaultAliases.add("Unspecified");
        return defaultAliases;
    }
    public Map<String, List<String>> getAliases() {
        return aliases;
    }
    /*
    public void addParam(String name){
        inputParams.add(name);
    }
    public void addSet(String name){
        inputParams.add(name);
    }
    public void addSets(Collection<String> sets){
        inputSets.addAll(sets);
    }
    public void addParams(Collection<String> params){
        inputParams.addAll(params);
    }
    */
}
