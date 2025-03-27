package Model.Data.Types;

import Exceptions.InternalErrors.ModelExceptions.Parsing.ParsingException;

import java.util.LinkedList;
import java.util.List;

public interface ModelType {
    boolean isCompatible(ModelType type);
    boolean isCompatible(String str);
    String toString();
    List<String> typeList();
    //dynamic dispatch somewhat doesnt work with static methods...
    static String convertArrayOfAtoms(String[] atoms, ModelType type){
        if(type instanceof Tuple)
            return Tuple.convertArrayOfAtoms(atoms,type);
        else    
            return ModelPrimitives.convertArrayOfAtoms(atoms,type);
    }

    /**
     * Converts a string formatted as an element into a list of atomic strings.
     * This method extracts atomic components from the input string based on specific formats:
     * 1. Strings enclosed in double quotes ("...") are treated as single atomic elements.
     * 2. Strings enclosed in angle brackets (<...>) are split into atomic components based on commas
     *    and recursively processed to handle nested structures.
     * Throws a {@link ParsingException} if the input string does not match either format.
     *
     * @param element the input string to be converted. It should be formatted as a quoted string ("...")
     *                or as a comma-separated list enclosed in angle brackets (<...>).
     * @return a list of strings representing atomic components extracted from the input string.
     * @throws ParsingException if the input string is not properly formatted as a quoted string or a valid list.
     */
    static List<List<String>> convertStringToAtoms(String element) {
        List<List<String>> ans = new LinkedList<>();
        if(element.matches("\".*\"")) {
            ans.add(List.of(element.substring(1, element.length() - 1)));
            return ans;
        }
        else if (element.matches("<.*>")){
            List<String> split = List.of(element.substring(1, element.length()-1).split(","));
            for(String subPart : split){
                ans.addAll(convertStringToAtoms(subPart));
            }
            return ans;
        } else if(element.matches("[a-zA-Z0-9]+")){
            ans.add(List.of(element));
            return ans;
        } else {
            //Note: throw exception or return an empty list?
            throw new ParsingException("Malformed Data Element");
        }

    }
}
