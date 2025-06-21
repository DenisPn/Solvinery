package Model;


import Image.Image;
import Image.Modules.Single.VariableModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Solution {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Solution.class);
    private static final Pattern statusPattern = Pattern.compile("SCIP Status +: +problem is solved.*optimal solution found");
    private static final Pattern solvingTimePattern = Pattern.compile("Solving Time \\(sec\\) +: +(\\d+(?:\\.\\d+)?)");
    // private static final Pattern solvingTimePattern = Pattern.compile("Solving Time \\(sec\\) +: +(\\d+\\.\\d+)");
    private static final Pattern objectiveValuePattern = Pattern.compile("objective value:\\s+(-?\\d+(\\.\\d+)?)");
    private static final Pattern variablePattern = Pattern.compile("^(.*?)[ \\t]+(-?\\d+)[ \\t]+\\(obj:(-?\\d+(?:\\.\\d+)?)\\)");
    boolean solved;

    /**
     *
     *  Maps variable names to a list of lists with each list holding elements of the solution,
     *  for example, for variable V of structure set1 * set2 where set1= {1,2} and set2= {"a","b"}
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
    @NonNull
    private final HashMap<String,String> objectiveValueMap;
    double solvingTime;
    /**
     *  the actual numeric value of the expression that was optimized
     */
    double objectiveValue;

    private boolean reachedSolution;
    private boolean reachedVariables;

    public Solution(){
        objectiveValueMap = new HashMap<>();
        variableSolution = new HashMap<>();
        variableStructure = new HashMap<>();
        rawVariableSolution = new HashMap<>();
        reachedSolution = false;
        reachedVariables = false;
        objectiveValue = -1F;
        solvingTime = -1F;
        solved = false;
    }

    public void processLine(@NonNull String line){
        if(reachedVariables) {
            processVariable(line);
        }
        else if(!reachedSolution) {
            if (statusPattern.matcher(line).find()) {
                reachedSolution = true;
                solved = true;
                log.debug("parsed solution, solution is solved");
            }
        }
        else {
            Matcher timeMatcher = solvingTimePattern.matcher(line);
            if (timeMatcher.find()) {
                solvingTime = Double.parseDouble(timeMatcher.group(1));
                log.debug("Parsed solving time");
            } else {
                Matcher objectiveMatcher = objectiveValuePattern.matcher(line);
                if (objectiveMatcher.find()) {
                    objectiveValue = Double.parseDouble(objectiveMatcher.group(1));
                    reachedVariables = true;
                    log.debug("Parsed total objective value");
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
                if(/*objectiveValue != 0 &&*/ !variableIdentifier.isBlank()) {
                    if (!rawVariableSolution.containsKey(variableIdentifier))
                        rawVariableSolution.put(variableIdentifier, new LinkedList<>());
                    rawVariableSolution.get(variableIdentifier).add(new VariableSolution(splitSolution, objectiveValue));
                }
            } else {
                log.error("Malformed variable structure detected in solution: {}", line);
            }
        }
    }
    public void postProcessSolution(@NonNull Image image){
        if(objectiveValue == -1F || solvingTime == -1F || !solved)
            throw new IllegalStateException(String.format(
                    "Solution state not valid for post process, objective value: %s, solving time: %s, Solve status: %s",objectiveValue,solvingTime,solved));
        for (VariableModule variable : image.getActiveVariables()) {
            variableStructure.put(variable.getAlias(),variable.getTypeStructure());
            if(rawVariableSolution.containsKey(variable.getName())) {
                variableSolution.put(variable.getAlias(), rawVariableSolution.get(variable.getName()));
                objectiveValueMap.put(variable.getAlias(), variable.getObjectiveValueAlias());
                rawVariableSolution.remove(variable.getName());
            }
        }
        if(!rawVariableSolution.isEmpty()){
            log.info("Ignored variables: {}",rawVariableSolution.keySet());
        }
    }

    public boolean isSolved() {
        return solved;
    }
    @Nullable
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
    @Nullable
    public List<String> getVariableStructure(String variableName) {
        return variableStructure.get(variableName);
    }
    @Nullable
    public String getObjectiveValueAlias(@NonNull String variableName) {
        return objectiveValueMap.get(variableName);
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
