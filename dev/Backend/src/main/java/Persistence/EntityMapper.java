package Persistence;

import Model.Data.Elements.Data.ModelSet;
import Model.Data.Types.ModelPrimitives;
import Model.Data.Types.ModelType;
import Model.Data.Types.Tuple;
import Persistence.Entities.Model.Data.ModelDataKeyPair;
import Persistence.Entities.Model.Data.ModelSetEntity;
import Persistence.Entities.UserEntity;
import User.User;

import java.util.List;
import java.util.UUID;

public class EntityMapper {

    public static User toDomain(UserEntity entity){
        return new User(entity.getUsername(),entity.getEmail());
    }

    /**
     * Convert a user to user entity, for persistence. Should be used only when registering a new user
     * Invalid parameters are accepted, and an exception will be thrown only when saving the user entity
     * @param user the user to convert
     * @param rawPassword the password, since password isn't used above persistence after registration.
     * @return a new UserEntity.
     * @see UserEntity
     * @see User
     */
    public static UserEntity toEntity(User user,String rawPassword){
       return new UserEntity(user.getUsername(),user.getEmail(),rawPassword);
    }
    public static ModelType toDomain(String type){
        List<List<String>> atoms=ModelType.convertStringToAtoms(type);
        if(atoms.size()==1){
            return ModelPrimitives.valueOf(atoms.getFirst().getFirst());
        }
        else {
            Tuple tuple=new Tuple();
            for(List<String> atom:atoms){
                ModelType innerType=toDomain(atom);
                tuple.append(innerType);
            }
            return tuple;
        }
    }
    public static ModelType toDomain(List<String> atoms){
        if(atoms.size()==1){
            return ModelPrimitives.valueOf(atoms.getFirst());
        }
        else {
            Tuple tuple=new Tuple();
            for(String atom:atoms){
                ModelType type=toDomain(atom);
                tuple.append(type);
            }
            return tuple;
        }
    }
    public static String toEntity(ModelType type){
        return type.toString();
    }
    public static ModelSetEntity toEntity(ModelSet modelSet, UUID imageId){
        ModelDataKeyPair key= new ModelDataKeyPair(imageId,modelSet.getName());
        return new ModelSetEntity(key,toEntity(modelSet.getType()),modelSet.getData());
    }
    public static ModelSet toDomain(ModelSetEntity entity){
        return new ModelSet(entity.getModelDataKey().getName(),
                toDomain(entity.getType()),entity.getData());
    }
}
