package Image.Modules.Single;

import org.springframework.lang.NonNull;

import java.util.List;

public class DataModule {

    @NonNull
    private final String structure;

    @NonNull
    private String alias;

    public DataModule(@NonNull List<String> structure, @NonNull String alias) {
        this.structure = convertList(structure);
        this.alias = alias;
    }
    public DataModule(@NonNull String structure, @NonNull String alias) {
        this.structure = structure;
        this.alias = alias;
    }
    
    @NonNull
    public List<String> getTypes() {
        if (!structure.startsWith("<") || !structure.endsWith(">")) {
            return List.of(structure);
        }
        String content = structure.substring(1, structure.length() - 1);
        return List.of(content.split(","));
    }
    @NonNull
    public  String getTypeString() {
        return structure;
    }

    @NonNull
    public static String convertList(List<String> structure) {
        if(structure.size() == 1) return structure.getFirst();
        else return "<" + String.join(",", structure) + ">";
    }
    @NonNull
    public String getAlias() {
        return alias;
    }
    public void setAlias(@NonNull String alias) {
        this.alias = alias;
    }

}
