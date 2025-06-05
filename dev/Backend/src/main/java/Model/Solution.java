package Model;

import Image.Image;
import Image.Modules.Single.VariableModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Slf4j
public class Solution {

    private static final Pattern statusPattern = Pattern.compile("SCIP Status\s+:\s+problem is solved.*optimal solution found");
    private static final Pattern solvingTimePattern = Pattern.compile("Solving Time \\(sec\\)\s+:\s+(\\d+\\.\\d+)");
    private static final Pattern objectiveValuePattern = Pattern.compile("objective value:\\s+(\\d+(\\.\\d+)?)");
    private static final Pattern variablePattern = Pattern.compile("^(.*?)[ \\t]+(\\d+)[ \\t]+\\(obj:(\\d+)\\)");


    boolean parsed;
    private String solution;
    boolean solved;

    /**
     *
     *  Maps variable names to a list of lists with each list holding elements of the solution,
     *  for example, for variable V of type set1 * set2 where set1= {1,2} and set2= {"a","b"}
     *  an example for a solution is: V -> { [1,"a"], [2, "b"] } note: order matters!
     *  Since from this point on this is only static data to be shown to the user, only Strings are in use
     *
     */
    final HashMap<String, List<VariableSolution>> variableSolution;
    final HashMap<String, List<VariableSolution>> rawVariableSolution;
    final HashMap<String, List<String>> variableStructure;
    final HashMap<String,List<String>> variableTypes;
    double solvingTime;
    /**
     *  the actual numeric value of the expression that was optimized
     */
    double objectiveValue;

    private boolean reachedSolution;
    private boolean reachedVariables;
    @Deprecated
    public Solution(String solution) {
        this.solution= solution;
        variableSolution = new HashMap<>();
        variableStructure = new HashMap<>();
        variableTypes = new HashMap<>();
        rawVariableSolution = new HashMap<>();
        parsed = false;
        reachedSolution = false;
        reachedVariables = false;

    }
    public Solution(){
        variableSolution = new HashMap<>();
        variableStructure = new HashMap<>();
        variableTypes = new HashMap<>();
        rawVariableSolution = new HashMap<>();
        reachedSolution = false;
        reachedVariables = false;
    }

    public void processLine(String line){
        if(reachedVariables)
            processVariable(line);
        else if(!reachedSolution) {
            if (statusPattern.matcher(line).find()) {
                reachedSolution = true;
                solved = true;
            }
        }
        else {
            Matcher timeMatcher = solvingTimePattern.matcher(line);
            if (timeMatcher.find()) {
                solvingTime = Double.parseDouble(timeMatcher.group(1));
            } else {
                Matcher objectiveMatcher = objectiveValuePattern.matcher(line);
                if (objectiveMatcher.find()) {
                    objectiveValue = Double.parseDouble(objectiveMatcher.group(1));
                    reachedVariables = true;
                }
            }
        }
    }
    private void processVariable(String line){
        if(!line.isBlank() && !line.startsWith("@@")) {
            Matcher variableMatcher = variablePattern.matcher(line);
            if (variableMatcher.find()) {
                String solution = variableMatcher.group(1);
                int objectiveValue = Integer.parseInt(variableMatcher.group(2));
                List<String> splitSolution = new LinkedList<>(Arrays.asList(solution.split("[#$]"))); //need a new array to remove dependence
                String variableIdentifier = splitSolution.getFirst();
                splitSolution.removeFirst();
                if (!rawVariableSolution.containsKey(variableIdentifier))
                    rawVariableSolution.put(variableIdentifier, new LinkedList<>());
                if (objectiveValue != 0)  //A 0 objective value means the solution part has no effect on the actual max/min expression
                    rawVariableSolution.get(variableIdentifier).add(new VariableSolution(splitSolution, objectiveValue));
            } else {
                log.error("Malformed variable structure detected in solution: {}", line);
            }
        }
    }
    public void postProcessSolution(Image image){
        for (VariableModule variable : image.getActiveVariables()) {
            variableStructure.put(variable.getAlias(), variable.getVariable().getBasicSets());
            variableTypes.put(variable.getAlias(),variable.getVariable().getStructure());
            Map<String,String> aliasMap = image.variableAliasMap();
            if(rawVariableSolution.containsKey(variable.getVariable().getName())) {
                variableSolution.put(variable.getAlias(), rawVariableSolution.get(variable.getVariable().getName()));
                rawVariableSolution.remove(variable.getVariable().getName());
            }
        }
        if(!rawVariableSolution.isEmpty()){
            log.info("Unprocessed variables: {}",rawVariableSolution.keySet());
        }
    }
    @Deprecated(forRemoval = true) //To be replaced with a line-by-line parse stream
    public void parseSolution(Image image) throws IOException {
        for (VariableModule variable : image.getActiveVariables()) {
                variableSolution.put(variable.getAlias(), new LinkedList<>());
                variableStructure.put(variable.getAlias(), variable.getVariable().getBasicSets());
                variableTypes.put(variable.getAlias(),variable.getVariable().getStructure());
        }
        try (BufferedReader reader = new BufferedReader(new StringReader(solution))) {
            String line;
            boolean solutionSection = false;


            while ((line = reader.readLine()) != null) {
                if (!solutionSection) {

                    Matcher statusMatcher = statusPattern.matcher(line);
                    if (statusMatcher.find()) {
                        solved = true;
                    }

                    Matcher solvingTimeMatcher = solvingTimePattern.matcher(line);
                    if (solvingTimeMatcher.find()) {
                        solvingTime = Double.parseDouble(solvingTimeMatcher.group(1));
                    }

                    Matcher objectiveMatcher = objectiveValuePattern.matcher(line);
                    if (objectiveMatcher.find()) {
                        objectiveValue = Double.parseDouble(objectiveMatcher.group(1));
                        solutionSection = true; // Objective value is defined right before the solution values section
                        parseSolutionValues(reader,image.variableAliasMap());
                    }
                }
            }
        }
        parsed=true;
    }
    @Deprecated(forRemoval = true) //To be replaced with a line-by-line parse stream
    private void parseSolutionValues(BufferedReader reader, Map<String,String> aliasMap) throws IOException {
        String line;
        while ((line = reader.readLine()) != null){
            Matcher variableMatcher = variablePattern.matcher(line);
            if (variableMatcher.find()) {
                String solution = variableMatcher.group(1);
                int objectiveValue = Integer.parseInt(variableMatcher.group(2));
                List<String> splitSolution = new LinkedList<>(Arrays.asList(solution.split("[#$]"))); //need a new array to remove dependence
                String variableIdentifier = splitSolution.getFirst();
                String variableAlias = aliasMap.get(variableIdentifier);
                splitSolution.removeFirst();
                if(variableSolution.containsKey(variableAlias)) { //A 0 objective value means the solution part has no effect on the actual max/min expression
                    if(objectiveValue != 0)
                        variableSolution.get(variableAlias).add(new VariableSolution(splitSolution,objectiveValue));
                }
                else {
                    log.error("Variable {} not found in image alias map, mapped value: {}",
                            variableIdentifier,variableAlias);
                }
            }
            else {
                log.error("Malformed variable structure detected in solution: {}", line);
            }
        }
    }
    @Deprecated(forRemoval = true) //after change parsing is assumed to always have been done if the object exists.
    public boolean parsed(){
        return parsed;
    }
    public boolean isSolved() {
        return solved;
    }

    public List<VariableSolution> getVariableSolution(String identifier) {
        return variableSolution.get(identifier);
    }


    public double getSolvingTime() {
        return solvingTime;
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }

    public Set<String> getActiveVariables() {
        return variableSolution.keySet();
    }

    public List<String> getVariableStructure(String variableName) {
        return variableStructure.get(variableName);
    }

    public List<String> getVariableTypes(String variableName) {
        return variableTypes.get(variableName);
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            String prettyRawVarSolution = mapper.writeValueAsString(rawVariableSolution);
            String prettyVarSolution = mapper.writeValueAsString(variableSolution);
            String prettyVarStructure = mapper.writeValueAsString(variableStructure);
            String prettyVarTypes = mapper.writeValueAsString(variableTypes);
            return "\nsolved: " + solved +
                    ",\nrawVariableSolution: " + prettyRawVarSolution +
                    ",\nvariableSolution: " + prettyVarSolution +
                    ",\nvariableStructure: " + prettyVarStructure +
                    ",\nvariableTypes: " + prettyVarTypes +
                    ",\nsolvingTime: " + solvingTime +
                    ",\nobjectiveValue: " + objectiveValue;
        } catch (Exception e) {
            log.warn("Failed to serialize solution object to string, returning default string instead: {}", e.getMessage());
            return super.toString(); // fallback to default toString if serialization fails

        }
    }
    public record VariableSolution(List<String> solution, int objectiveValue ){}
}
