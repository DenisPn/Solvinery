package Exceptions.ErrorData;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for Zimpl error codes and their corresponding messages.
 * These error codes and messages are directly based on the Zimpl documentation.
 * I didn't do this manually and used an LLM - data here should be fine, but take that into account
 */
public enum ZimplErrorType {
    /** 101 Bad filename: The name given with the -o option is either missing, a directory name, or starts with a dot. */
    BAD_FILENAME(101, "The name given with the -o option is either missing, a directory name, or starts with a dot."),
    /** 102 File write error: Some error occurred when writing to an output file. A description of the error follows on the next line. For the meaning consult your OS documentation. */
    FILE_WRITE_ERROR(102, "Some error occurred when writing to an output file. A description of the error follows on the next line. For the meaning consult your OS documentation."),
    /** 103 Output format not supported, using LP format: You tried to select another format than lp, mps, hum, rlp, or pip. */
    UNSUPPORTED_FORMAT(103, "You tried to select another format than lp, mps, hum, rlp, or pip."),
    /** 104 File open failed: Some error occurred when trying to open a file for writing. A description of the error follows on the next line. For the meaning consult your OS documentation. */
    FILE_OPEN_FAILED(104, "Some error occurred when trying to open a file for writing. A description of the error follows on the next line. For the meaning consult your OS documentation."),
    /** 105 Duplicate constraint name "xxx": Two subto statements have the same name. */
    DUPLICATE_CONSTRAINT(105, "Two subto statements have the same name."),
    /** 106 Empty LHS, constraint trivially violated: One side of your constraint is empty and the other not equal to zero. Most frequently this happens, when a set to be summed up is empty. */
    EMPTY_LHS(106, "One side of your constraint is empty and the other not equal to zero. Most frequently this happens, when a set to be summed up is empty."),
    /** 107 Range must be l ≤ x ≤ u, or u > x > l: If you specify a range you must have the same comparison operators on both sides. */
    INVALID_RANGE(107, "If you specify a range you must have the same comparison operators on both sides."),
    /** 108 Empty Term with nonempty LHS/RHS, constraint trivially violated: The middle of your constraint is empty and either the left- or right-hand side of the range is not zero. This most frequently happens, when a set to be summed up is empty. */
    EMPTY_TERM(108, "The middle of your constraint is empty and either the left- or right-hand side of the range is not zero. This most frequently happens, when a set to be summed up is empty."),
    /** 109 LHS/RHS contradiction, constraint trivially violated: The lower side of your range is bigger than the upper side, e.g. 15 ≤ x ≤ 2. */
    CONTRADICTORY_CONSTRAINT(109, "The lower side of your range is bigger than the upper side, e.g. 15 ≤ x ≤ 2."),
    /** 110 Division by zero: You tried to divide by zero. This is not a good idea. */
    DIVISION_BY_ZERO(110, "You tried to divide by zero. This is not a good idea."),
    /** 111 Modulo by zero: You tried to compute a number modulo zero. This does not work well. */
    MODULO_BY_ZERO(111, "You tried to compute a number modulo zero. This does not work well."),
    /** 112 Exponent value xxx is too big or not an integer: It is only allowed to raise a number to the power of integers. Also trying to raise a number to the power of more than two billion is prohibited. */
    INVALID_EXPONENT(112, "It is only allowed to raise a number to the power of integers. Also trying to raise a number to the power of more than two billion is prohibited."),
    /** 113 Factorial value xxx is too big or not an integer: You can only compute the factorial of integers. Also computing the factorial of a number bigger than two billion is generally a bad idea. */
    INVALID_FACTORIAL(113, "You can only compute the factorial of integers. Also computing the factorial of a number bigger than two billion is generally a bad idea."),
    /** 114 Negative factorial value: To compute the factorial of a number it has to be positive. */
    NEGATIVE_FACTORIAL(114, "To compute the factorial of a number it has to be positive."),
    /** 115 Timeout!: You tried to compute a number bigger than 1000!. */
    TIMEOUT(115, "You tried to compute a number bigger than 1000!."),
    /** 116 Illegal value type in min: xxx only numbers are possible: You tried to build the minimum of some strings. */
    INVALID_MIN_TYPE(116, "You tried to build the minimum of some strings."),
    /** 117 Illegal value type in max: xxx only numbers are possible: You tried to build the maximum of some strings. */
    INVALID_MAX_TYPE(117, "You tried to build the maximum of some strings."),
    /** 118 Comparison of different types: You tried to compare apples with oranges, i.e., numbers with strings. */
    TYPE_COMPARISON_ERROR(118, "You tried to compare apples with oranges, i.e., numbers with strings."),
    /** 119 Operation on sets with different dimensions: You tried to apply a set operation (e.g., union, minus) on sets with different dimensions. */
    SET_DIMENSION_MISMATCH(119, "You tried to apply a set operation (e.g., union, minus) on sets with different dimensions."),
    /** 120 Minus of incompatible sets: To apply Operation xxx (union, minus, intersection, symmetric difference) on two sets, both must have tuples of the same type, i.e., the components of the tuples must have the same type (number, string). */
    INCOMPATIBLE_SETS(120, "To apply Operation xxx (union, minus, intersection, symmetric difference) on two sets, both must have tuples of the same type, i.e., the components of the tuples must have the same type (number, string)."),
    /** 121 Negative exponent on variable: The exponent to a variable was negative. This is not supported. */
    NEGATIVE_EXPONENT(121, "The exponent to a variable was negative. This is not supported."),
    /** 133 Unknown symbol: A name was used that is not defined anywhere in scope. */
    UNKNOWN_SYMBOL(133, "A name was used that is not defined anywhere in scope."),
    /** 197 Empty index set for set: The index set for the set declaration is empty. */
    EMPTY_INDEX_SET(197, "The index set for the set declaration is empty."),
    /** 221 The objective function has to be linear: Only objective functions with linear constraints are allowed. */
    OBJECTIVE_MUST_BE_LINEAR(221, "Only objective functions with linear constraints are allowed."),
    /** 800 Parse error: expecting xxx (or yyy): Parsing error. What was found was not what was expected. The statement you entered is not valid. */
    PARSER_ERROR(800, "Parsing error. What was found was not what was expected. The statement you entered is not valid."),
    /** 801 Parser failed: The parsing routine failed. This should not happen. */
    PARSER_FAILED(801, "The parsing routine failed. This should not happen."),
    /** 802 Regular expression error: A regular expression given to the match parameter of a read statement was not valid. See error messages for details. */
    REGEX_ERROR(802, "A regular expression given to the match parameter of a read statement was not valid. See error messages for details."),
    /** 803 String too long: The program encountered a string which is larger than 1 GB. */
    STRING_TOO_LONG(803, "The program encountered a string which is larger than 1 GB."),
    /** 900 Check failed: A check instruction did not evaluate to true. */
    CHECK_FAILED(900, "A check instruction did not evaluate to true.");

    private final int code;
    private final String message;

    private static final Map<Integer, ZimplErrorType> CodeToEnumMap = new HashMap<>();

    // Populate the map at class loading time
    //This code is executed only once, not for every enum created- neat.
    static {
        for (ZimplErrorType errorType : ZimplErrorType.values()) {
            CodeToEnumMap.put(errorType.code, errorType);
        }
    }

    ZimplErrorType (int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Retrieves the corresponding ZimplErrorType for the provided error code.
     *
     * @param code the integer code representing a specific ZimplErrorType
     * @return the ZimplErrorType associated with the given code
     * @throws IllegalArgumentException if the code does not map to any known ZimplErrorType
     */
    public static ZimplErrorType fromCode(int code) {
        if(CodeToEnumMap.containsKey(code)){
            return CodeToEnumMap.get(code);
        }
        throw new IllegalArgumentException("Unknown error code: " + code);
    }
}