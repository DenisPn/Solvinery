package Model.Data.Types;

import org.jetbrains.annotations.Nullable;
import org.springframework.lang.NonNull;

import java.util.*;

public class Tuple implements ModelType {
    private final List<ModelType> val;


    public Tuple() {
        val = new ArrayList<>();
    }

    public List<ModelType> getTypes(){
        return val;
    }
    @Override
    public boolean primitive(){
        return false;
    }
    public boolean isCompatible(ModelType p){
        if(p instanceof Tuple){
            if(((Tuple)p).val.size() == this.val.size()){
                Iterator<ModelType> it1 = ((Tuple)p).val.iterator();
                Iterator<ModelType> it2 = val.iterator();
                while(it1.hasNext()){
                    if(it1.next() != it2.next())
                        return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean isCompatible(@Nullable String str) {

        if (str == null || str.length() < 2 || !str.startsWith("<") || !str.endsWith(">")) {
            return false;
        }
        
        
        String content = str.substring(1, str.length() - 1).trim();
        
        
        if (content.isEmpty()) {
            return val.isEmpty();
        }
        

        String[] elements = content.split(",");
        

        if (elements.length != val.size()) {
            return false;
        }
        
        for (int i = 0; i < elements.length; i++) {
            String element = elements[i].trim();
            ModelType currentPrimitive = val.get(i);
            
            if (!currentPrimitive.isCompatible(element)) {
                return false;
            }
        }
        
        return true;
    }

    public void append(ModelType tmp) {
        if(tmp instanceof ModelPrimitives)
            val.add((ModelPrimitives)tmp);
        else {
            val.addAll(((Tuple) tmp).getTypes());
        }

    }

    @NonNull
    public String toString() {
        if (val.isEmpty()) {
            return "<>";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append('<');
        
        for (int i = 0; i < val.size(); i++) {
            sb.append(val.get(i).toString());
            if (i < val.size() - 1) {
                sb.append(",");
            }
        }
        
        sb.append('>');
        return sb.toString();
    }
    @NonNull
    @Override
    public List<String> typeList(){
        List<String> types = new LinkedList<>();
        for(ModelType primitives:val){
            types.add(primitives.toString());
        }
        return types;
    }
    public int size() {
        return this.val.size();
    }

    
    @NonNull
    public static String convertArrayOfAtoms(@NonNull String[] atoms, ModelType type) {
        Tuple tup = ((Tuple)type);
        StringBuilder ans = new StringBuilder();
        for(int i = 0; i < atoms.length ; i++){
            ans.append(ModelPrimitives.convertArrayOfAtoms(new String[]{atoms[i]}, tup.getTypes().get(i))).append(",");
        }
        ans = new StringBuilder(ans.substring(0, ans.length() - 1));
        if(atoms.length == 1)
            return ans.toString();
        return "<" + ans + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Tuple tuple = (Tuple) o;
        return Objects.equals(val, tuple.val);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(val);
    }
}

