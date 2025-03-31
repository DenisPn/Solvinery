package Image.Modules;

import Model.Data.Elements.Variable;

import java.util.*;

public class VariableModule {

    final Map<String, Variable> variables;
    /*final Set<String> inputSets;
    final Set<String> inputParams;*/
    final Map<String, List<String>> aliases;

    public Map<String, Variable> getVariables() {
        return variables;
    }

   /* public Set<String> getInputSets() {
        return inputSets;
    }

    public Set<String> getInputParams() {
        return inputParams;
    }*/

    public VariableModule() {
        variables = new HashMap<>();
        /*inputSets = new HashSet<>();
        inputParams = new HashSet<>();*/
        aliases = new HashMap<>();
    }

    public VariableModule(Map<String, Variable> variables, Collection<String> inputSets, Collection<String> inputParams,Map<String,List<String>> aliases) {
        this.variables = new HashMap<>(variables);
        /*this.inputSets = new HashSet<>(inputSets);
        this.inputParams = new HashSet<>(inputParams);*/
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
    public VariableModule(Set<Variable> variables, Collection<String> inputSets, Collection<String> inputParams,Map<String,List<String>> aliases) {
        this.variables = new HashMap<>();
        for(Variable variable: variables) {
            this.variables.put(variable.getName(),variable);
        }
        /*this.inputSets = new HashSet<>(inputSets);
        this.inputParams = new HashSet<>(inputParams);*/
        this.aliases = new HashMap<>(aliases);
        for (Variable variable : variables) {
            String variableName = variable.getName();
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
       /* inputSets.clear();
        inputParams.clear();*/
    }

    public void override(Map<String, Variable> variables/* Collection<String> inputSets, Collection<String> inputParams */,Map<String,List<String>> aliases) {
        this.variables.clear();
        /*this.inputSets.clear();
        this.inputParams.clear();*/
        this.variables.putAll(variables);
        /*this.inputSets.addAll(inputSets);
        this.inputParams.addAll(inputParams);*/
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
    public Variable get(String name) {
        return variables.get(name);
    }

    public void addVariable(Variable variable) {
        variables.put(variable.getName(),variable);
        if(!aliases.containsKey(variable.getName())) {
            aliases.put(variable.getName(), getDefaultAliases(variable));
        }
    }
    private List<String> getDefaultAliases(Variable variable) {
        /*ArrayList<String> defaultAliases = new ArrayList<>();
        for (ModelSet modelSet : variable.getSetDependencies()) {
            for (ModelInput.StructureBlock block : modelSet.getStructure()) {
                defaultAliases.add(block.dependency.getIdentifier());
            }
        }
        if (defaultAliases.isEmpty())
            defaultAliases.add("Unspecified");
        return defaultAliases;*/
        return new ArrayList<>(); //TODO tmp fix
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
