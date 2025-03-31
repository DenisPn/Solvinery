package Image.Modules;

import Model.Data.Elements.Variable;

import java.util.*;

public class VariableModule {

    final Map<String, Variable> variables;
    /*final Set<String> inputSets;
    final Set<String> inputParams;*/
    final Map<String, String> aliases;

    public Map<String, Variable> getVariables() {
        return variables;
    }

    public VariableModule() {
        variables = new HashMap<>();
        aliases = new HashMap<>();
    }

    public VariableModule(Map<String, Variable> variables,Map<String,String> aliases) {
        this.variables = new HashMap<>(variables);
        this.aliases = new HashMap<>(aliases);
        for (String variableName : variables.keySet()) {
            if(aliases.containsKey(variableName)) {
                this.aliases.put(variableName, aliases.get(variableName));
            }
            else{
                //if no specific alies is specified, alies will default to set names that make up the set
                this.aliases.put(variableName, getDefaultAlias(variables.get(variableName)));
            }
        }
    }
    public VariableModule(Set<Variable> variables,Map<String,String> aliases) {
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
                this.aliases.put(variableName, getDefaultAlias(variable));
            }
        }
    }

    public void clear() {
        variables.clear();
    }

    public void override(Map<String, Variable> variables,Map<String,String> aliases) {
        this.variables.clear();
        this.variables.putAll(variables);
        for (String variableName : variables.keySet()) {
            if(aliases.containsKey(variableName)) {
                this.aliases.put(variableName, aliases.get(variableName));
            }
            else{
                this.aliases.put(variableName, getDefaultAlias(variables.get(variableName)));
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
            aliases.put(variable.getName(), getDefaultAlias(variable));
        }
    }
    private String getDefaultAlias(Variable variable) {
        return variable.getName();
    }
    public Map<String, String> getAliases() {
        return aliases;
    }
}
