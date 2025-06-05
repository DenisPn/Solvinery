package Model;

import Image.Image;
import Image.Modules.Single.VariableModule;
import Model.Data.Elements.Variable;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.util.Tuple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.*;
@Slf4j
public class Solution {

    private static final Pattern statusPattern = Pattern.compile("SCIP Status\s+:\s+problem is solved.*optimal solution found");
    private static final Pattern solvingTimePattern = Pattern.compile("Solving Time \\(sec\\)\s+:\s+(\\d+\\.\\d+)");
    private static final Pattern objectiveValuePattern = Pattern.compile("objective value:\\s+(\\d+(\\.\\d+)?)");
    private static final Pattern variablePattern = Pattern.compile("^(.*?)[ \\t]+(\\d+)[ \\t]+\\(obj:(\\d+)\\)");

    //final String engineMsg;
    //final boolean engineRunSuccess;
    boolean parsed;
    //String solutionPath;
    private final String solution;
    boolean solved;

    /**
     *
     *  Maps variable names to a list of lists with each list holding elements of the solution,
     *  for example, for variable V of type set1 * set2 where set1= {1,2} and set2= {"a","b"}
     *  an example for a solution is: V -> { [1,"a"], [2, "b"] } note: order matters!
     *  Since from this point on this is only static data to be shown to the user, only Strings are in use
     *
     */
    final HashMap<String, List<Tuple<List<String>, Integer>>> variableSolution;
    final HashMap<String, List<String>> variableStructure;
    final HashMap<String,List<String>> variableTypes;
    double solvingTime;
    /**
     *  the actual numeric value of the expression that was optimized
     */
    double objectiveValue;


    public Solution(String solution) {
        this.solution= solution;
        variableSolution = new HashMap<>();
        variableStructure = new HashMap<>();
        variableTypes = new HashMap<>();
        parsed = false;
    }

    public void parseSolution(Image image) throws IOException {
        for (VariableModule variable : image.getActiveVariables()) {
                variableSolution.put(variable.getAlias(), new LinkedList<>());
                variableStructure.put(variable.getAlias(), variable.getVariable().getStructure());
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
                        parseSolutionValues(reader);
                    }
                }
            }
        }
        parsed=true;
    }

    private void parseSolutionValues(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null){
            Matcher variableMatcher = variablePattern.matcher(line);
            if (variableMatcher.find()) {
                String solution = variableMatcher.group(1);
                int objectiveValue = Integer.parseInt(variableMatcher.group(2));
                List<String> splitSolution = new LinkedList<>(Arrays.asList(solution.split("[#$]"))); //need a new array to remove dependence
                String variableIdentifier = splitSolution.getFirst();
                splitSolution.removeFirst();
                if(variableSolution.containsKey(variableIdentifier) && objectiveValue!=0) { //A 0 objective value means the solution part has no effect on the actual max/min expression
                        variableSolution.get(variableIdentifier).add(new Tuple<>(splitSolution,objectiveValue));
                }
            }
            else {
                log.error("Malformed variable structure detected in solution: {}", line);
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

    public Set<String> getActiveVariables() {
        return variableSolution.keySet();
    }

    public List<String> getVariableStructure(String variableName) {
        return variableStructure.get(variableName);
    }

    public boolean isParsed() {
        return parsed;
    }

    public HashMap<String, List<String>> getVariableTypes() {
        return variableTypes;
    }
    public List<String> getVariableTypes(String variableName) {
        return variableTypes.get(variableName);
    }
}
