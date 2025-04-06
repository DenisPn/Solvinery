package Persistence;

import Image.Image;
import Image.Modules.Operational.ConstraintModule;
import Image.Modules.Operational.PreferenceModule;
import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Elements.Variable;
import Model.Data.Types.ModelPrimitives;
import Model.Data.Types.ModelType;
import Model.Data.Types.Tuple;
import Model.Model;
import Persistence.Entities.Image.Data.ModelParamEntity;
import Persistence.Entities.Image.Data.VariableEntity;
import Persistence.Entities.Image.ImageComponentKey;
import Persistence.Entities.Image.Data.ModelSetEntity;
import Persistence.Entities.Image.ImageEntity;
import Persistence.Entities.Image.Operational.ConstraintEntity;
import Persistence.Entities.Image.Operational.ConstraintModuleEntity;
import Persistence.Entities.Image.Operational.PreferenceEntity;
import Persistence.Entities.Image.Operational.PreferenceModuleEntity;
import Persistence.Entities.UserEntity;
import User.User;
import org.apache.tomcat.util.bcel.Const;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        ImageComponentKey key= new ImageComponentKey(imageId,modelSet.getName());
        return new ModelSetEntity(key,toEntity(modelSet.getType()),modelSet.getData());
    }
    public static ModelSet toDomain(ModelSetEntity entity){
        return new ModelSet(entity.getModelDataKey().getName(),
                toDomain(entity.getType()),entity.getData());
    }
    public static ModelParamEntity toEntity(ModelParameter parameter, UUID imageId){
        return new ModelParamEntity(imageId, parameter.getName(), parameter.getType().toString(), parameter.getData(), parameter.getAlias());
    }
    public static ModelParameter toDomain(ModelParamEntity entity){
        return new ModelParameter(entity.getName(),toDomain(entity.getType()),entity.getData(),entity.getAlias());
    }
    public static Constraint toDomain(ConstraintEntity entity){
        return new Constraint(entity.getName());
    }
    public static ConstraintEntity toEntity(Constraint constraint){
        return new ConstraintEntity(constraint.getName());
    }

    public static Set<ConstraintEntity> toConstraintsEntities (Collection<Constraint> constraints) {
        return constraints.stream()
                .map(EntityMapper::toEntity)
                .collect(Collectors.toSet());
    }
    public static Set<Constraint> toConstraints (Collection<ConstraintEntity> entities) {
        return entities.stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toSet());
    }
    public static Set<Preference> toPreferences (Collection<PreferenceEntity> entities) {
        return entities.stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toSet());
    }
    public static Preference toDomain(PreferenceEntity entity){
        return new Preference(entity.getName());
    }
    public static PreferenceEntity toEntity(Preference preference){
        return new PreferenceEntity(preference.getName());
    }

    public static Set<PreferenceEntity> toPreferencesEntities (Collection<Preference> preferences) {
        return preferences.stream()
                .map(EntityMapper::toEntity)
                .collect(Collectors.toSet());
    }

    public static Set<Variable> toVariables (Collection<VariableEntity> entities) {
        return entities.stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toSet());
    }

    public static Set<VariableEntity> toVariableEntities (Collection<Variable> variables, UUID imageId) {
        return variables.stream()
                .map(variable -> EntityMapper.toEntity(variable, imageId))
                .collect(Collectors.toSet());
    }
    public static Set<ConstraintModuleEntity> toConstraintModuleEntities (Collection<ConstraintModule> constraintModules, UUID imageId) {
        return constraintModules.stream()
                .map(variable -> EntityMapper.toEntity(variable, imageId))
                .collect(Collectors.toSet());
    }
    public static Set<PreferenceModuleEntity> toPreferenceModuleEntities (Collection<PreferenceModule> preferenceModules, UUID imageId) {
        return preferenceModules.stream()
                .map(variable -> EntityMapper.toEntity(variable, imageId))
                .collect(Collectors.toSet());
    }

    public static Set<ConstraintModule> toConstraintModules (Collection<ConstraintModuleEntity> entities) {
        return entities.stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toSet());
    }
    public static Set<PreferenceModule> toPreferenceModules (Collection<PreferenceModuleEntity> entities) {
        return entities.stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toSet());
    }

    public static Set<ModelSet> toModelSets (Collection<ModelSetEntity> entities) {
        return entities.stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toSet());
    }
    public static Set<ModelParameter> toModelParams (Collection<ModelParamEntity> entities) {
        return entities.stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toSet());
    }
    public static Set<ModelSetEntity> toModelSetEntities (Collection<ModelSet> modelSets, UUID imageId) {
        return modelSets.stream()
                .map(modelSet -> EntityMapper.toEntity(modelSet, imageId)).collect(Collectors.toSet());
    }
    public static Set<ModelParamEntity> toModelParamEntities (Collection<ModelParameter> modelParameters, UUID imageId) {
        return modelParameters.stream()
                .map(modelSet -> EntityMapper.toEntity(modelSet, imageId)).collect(Collectors.toSet());
    }
    public static ConstraintModuleEntity toEntity(ConstraintModule module, UUID imageId){
        return new ConstraintModuleEntity(imageId, module.getName(), module.getDescription(), toConstraintsEntities(module.getConstraints().values()));
    }
    public static ConstraintModule toDomain(ConstraintModuleEntity entity){
        return new ConstraintModule(entity.getName(), entity.getDescription(), toConstraints(entity.getConstraints()));
    }
    public static PreferenceModuleEntity toEntity(PreferenceModule module, UUID imageId){
        return new PreferenceModuleEntity(imageId, module.getName(), module.getDescription(), toPreferencesEntities(module.getPreferences().values()));
    }
    public static PreferenceModule toDomain(PreferenceModuleEntity entity){
        return new PreferenceModule(entity.getName(), entity.getDescription(), toPreferences(entity.getPreferences()));
    }
    public static Variable toDomain(VariableEntity entity){
        return new Variable(entity.getName(),entity.getStructure(),entity.getAlias());
    }
    public static VariableEntity toEntity(Variable entity, UUID imageId){
        return new VariableEntity(imageId,entity.getName(),entity.getStructure(),entity.getAlias());
    }
    public static Image toDomain(ImageEntity imageEntity){
        return new Image(imageEntity.getZimplCode(),toConstraintModules(imageEntity.getConstraintModules()),
                toPreferenceModules(imageEntity.getPreferenceModules()), toModelSets(imageEntity.getActiveSets()),
                toModelParams(imageEntity.getActiveParams()), toVariables(imageEntity.getVariables()));
    }
    public static ImageEntity toEntity(Image image, UUID imageId){
        return new ImageEntity(toPreferenceModuleEntities(image.getPreferenceModules().values(),imageId),
                toConstraintModuleEntities(image.getConstraintsModules().values(), imageId),
                toVariableEntities(image.getActiveVariables(),imageId),
                toModelParamEntities(image.getActiveParams(),imageId),
                toModelSetEntities(image.getActiveSets(),imageId),
                image.getSourceCode());
    }
    
}
