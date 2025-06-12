package Model;


import Image.Image;
import Image.Modules.Single.VariableModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Slf4j
public class Solution {

    private static final Pattern statusPattern = Pattern.compile("SCIP Status +: +problem is solved.*optimal solution found");
    private static final Pattern solvingTimePattern = Pattern.compile("Solving Time \\(sec\\) +: +(\\d+\\.\\d+)");
    private static final Pattern objectiveValuePattern = Pattern.compile("objective value:\\s+(\\d+(\\.\\d+)?)");
    private static final Pattern variablePattern = Pattern.compile("^(.*?)[ \\t]+(\\d+)[ \\t]+\\(obj:(\\d+)\\)");
    boolean solved;

    /**
     *
     *  Maps variable names to a list of lists with each list holding elements of the solution,
     *  for example, for variable V of type set1 * set2 where set1= {1,2} and set2= {"a","b"}
     *  an example for a solution is: V -> { [1,"a"], [2, "b"] } note: order matters!
     *  Since from this point on this is only static data to be shown to the user, only Strings are in use
     *
     */
    @NonNull
    final HashMap<String, List<VariableSolution>> variableSolution;
    @NonNull
    final HashMap<String, List<VariableSolution>> rawVariableSolution;
    @NonNull
    final HashMap<String,List<String>> variableStructure;
    double solvingTime;
    /**
     *  the actual numeric value of the expression that was optimized
     */
    double objectiveValue;

    private boolean reachedSolution;
    private boolean reachedVariables;

    public Solution(){
        variableSolution = new HashMap<>();
        variableStructure = new HashMap<>();
        rawVariableSolution = new HashMap<>();
        reachedSolution = false;
        reachedVariables = false;
    }

    public void processLine(@NonNull String line){
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
    private void processVariable(@NonNull String line){
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
    public void postProcessSolution(@NonNull Image image){
        for (VariableModule variable : image.getActiveVariables()) {
            variableStructure.put(variable.getAlias(),variable.getTypeStructure());
            if(rawVariableSolution.containsKey(variable.getName())) {
                variableSolution.put(variable.getAlias(), rawVariableSolution.get(variable.getName()));
                rawVariableSolution.remove(variable.getName());
            }
        }
        if(!rawVariableSolution.isEmpty()){
            log.info("Unprocessed variables: {}",rawVariableSolution.keySet());
        }
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

    @NonNull
    public Set<String> getActiveVariables() {
        return variableSolution.keySet();
    }

    public List<String> getVariableStructure(String variableName) {
        return variableStructure.get(variableName);
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            String prettyRawVarSolution = mapper.writeValueAsString(rawVariableSolution);
            String prettyVarSolution = mapper.writeValueAsString(variableSolution);
            String prettyVarStructure = mapper.writeValueAsString(variableStructure);
            String prettyVarTypes = mapper.writeValueAsString(variableStructure);
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
