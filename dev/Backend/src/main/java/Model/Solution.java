package Model;

import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Variable;
import org.yaml.snakeyaml.util.Tuple;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

public class Solution {
    final String engineMsg;
    final boolean engineRunSuccess;
    boolean parsed;
    final String solutionPath;
    boolean solved;
    /*
    maps variable names to a list of lists with each list holding elements of the solution,
    for example for variable V of type set1 * set2 where set1= {1,2} and set2= {"a","b"}
    an example for a solution is: V -> { [1,"a"], [2, "b"] } note: order matters!
    Since from this point on this is only static data to be shown to the user, only Strings are in use
     */
    final HashMap<String, List<Tuple<List<String>, Integer>>> variableSolution;
    final HashMap<String, List<String>> variableStructure;
    final HashMap<String, List<String>> variableTypes;
    double solvingTime;
    double objectiveValue; // the actual numeric value of the expression that was optimized


    //helper fields
    Collection<Variable> variables;

    public Solution(String solutionPath) {
        this.solutionPath = solutionPath;
        variableSolution = new HashMap<>();
        variableStructure = new HashMap<>();
        parsed = false;
        engineRunSuccess = true;
        engineMsg = "";
        variableTypes = new HashMap<>();
    }
    public Solution(String solutionPath,String engineMsg,boolean engineRunSuccess) {
        this.solutionPath = solutionPath;
        variableSolution = new HashMap<>();
        variableStructure = new HashMap<>();
        parsed = false;
        this.engineMsg = engineMsg;
        this.engineRunSuccess = engineRunSuccess;
        variableTypes = new HashMap<>();
    }

    public List<String> getVariableTypes(String identifier) {
        return variableTypes.get(identifier);
    }

    //Implement as lazy call or run during initialization?
    public void parseSolution(ModelInterface model, Set<String> varsToParse,Map<String,List<String>> aliases) throws IOException {
        variables = model.getVariables(varsToParse);
        for (Variable variable : variables) {
            if (varsToParse.contains(variable.getName())) {
                variableSolution.put(variable.getName(), new ArrayList<>());
                if(aliases.containsKey(variable.getName())) {
                    variableStructure.put(variable.getName(), aliases.get(variable.getName()));
                    variableTypes.put(variable.getName(), new ArrayList<>());
                    /*for (ModelSet modelSet : variable.getSetDependencies()) {
                        variableTypes.get(variable.getName()).add(modelSet.getType().toString());
                    }*/
                }
                else {
                    variableStructure.put(variable.getName(), new ArrayList<>());
                    variableTypes.put(variable.getName(), new ArrayList<>());
                    //below lines are not solution dependent but problem dependent, will be more efficient to maintain them inside the image
                    /*for (ModelSet modelSet : variable.getSetDependencies()) {
                        variableTypes.get(variable.getName()).add(modelSet.getType().toString());
                        *//*for (ModelInput.StructureBlock block : modelSet.getStructure()) {
                            if (block.dependency != null) //fix?
                                variableStructure.get(variable.getName()).add(block.dependency.identifier);
                        }*//*
                    }*/
                }
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(solutionPath))) {
            String line;
            boolean solutionSection = false;

            Pattern statusPattern = Pattern.compile("SCIP Status\s+:\s+problem is solved.*optimal solution found");
            Pattern solvingTimePattern = Pattern.compile("Solving Time \\(sec\\)\s+:\s+(\\d+\\.\\d+)");
            Pattern objectiveValuePattern = Pattern.compile("objective value:\\s+(\\d+(\\.\\d+)?)");
            while ((line = reader.readLine()) != null) {
                if (!solutionSection) {
                    // Check for the solved status
                    Matcher statusMatcher = statusPattern.matcher(line);
                    if (statusMatcher.find()) {
                        solved = true;
                    }
                    // Extract solving time
                    Matcher solvingTimeMatcher = solvingTimePattern.matcher(line);
                    if (solvingTimeMatcher.find()) {
                        solvingTime = Double.parseDouble(solvingTimeMatcher.group(1));
                    }
                    // Extract objective value
                    Matcher objectiveMatcher = objectiveValuePattern.matcher(line);
                    if (objectiveMatcher.find()) {
                        objectiveValue = Double.parseDouble(objectiveMatcher.group(1));
                        solutionSection = true; // Objective value is defined right before the solution values section
                        parseSolutionValues(reader, varsToParse);
                    }
                }
//              else {
//                    parseSolutionValues(reader,varsToParse);
//                }
            }
        }
        parsed=true;
    }
    private void parseSolutionValues(BufferedReader reader, Set<String> varsToParse) throws IOException {
        Pattern variablePattern = Pattern.compile("^(.*?)[ \\t]+(\\d+)[ \\t]+\\(obj:(\\d+)\\)");
        String line;
        while ((line = reader.readLine()) != null){
            Matcher variableMatcher = variablePattern.matcher(line);
            if (variableMatcher.find()) {
                String solution = variableMatcher.group(1);
                int objectiveValue = Integer.parseInt(variableMatcher.group(2));
                List<String> splitSolution = new LinkedList<>(Arrays.asList(solution.split("[#$]"))); //need a new array to remove dependence
                String variableIdentifier = splitSolution.getFirst();
                splitSolution.removeFirst();
                if(varsToParse.contains(variableIdentifier) && objectiveValue!=0) { //A 0 objective value means the solution part has no effect on the actual max/min expression
                    variableSolution.get(variableIdentifier).add(new Tuple<>(splitSolution,objectiveValue));
                }
            }
            else {
                //TODO: log or throw exception?
            }
        }
    }
    public boolean parsed(){
        return parsed;
    }

    public boolean isSolved() {
        return solved;
    }

    public HashMap<String, List<Tuple<List<String>, Integer>>> getVariableSolution() {
        return variableSolution;
    }

    public List<Tuple<List<String>, Integer>> getVariableSolution(String identifier) {
        return variableSolution.get(identifier);
    }

    public HashMap<String, List<String>> getVariableStructure() {
        return variableStructure;
    }

    public double getSolvingTime() {
        return solvingTime;
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }

    public Collection<Variable> getVariables() {
        return variables;
    }

    public List<String> getVariableStructure(String variableName) {
        return variableStructure.get(variableName);
    }
}
