package Persistence;

import Image.Image;
import Image.Modules.Single.ParameterModule;
import Image.Modules.Single.SetModule;
import Image.Modules.Grouping.ConstraintModule;
import Image.Modules.Grouping.PreferenceModule;
import Image.Modules.Single.VariableModule;
import Model.Data.Elements.Data.ModelParameter;
import Model.Data.Elements.Data.ModelSet;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Elements.Variable;
import Model.Data.Types.ModelPrimitives;
import Model.Data.Types.ModelType;
import Model.Data.Types.Tuple;
import Persistence.Entities.Image.Data.ParameterEntity;
import Persistence.Entities.Image.Data.VariableEntity;
import Persistence.Entities.Image.ImageComponentKey;
import Persistence.Entities.Image.Data.SetEntity;
import Persistence.Entities.Image.ImageEntity;
import Persistence.Entities.Image.Operational.ConstraintEntity;
import Persistence.Entities.Image.Operational.ConstraintModuleEntity;
import Persistence.Entities.Image.Operational.PreferenceEntity;
import Persistence.Entities.Image.Operational.PreferenceModuleEntity;
import Persistence.Entities.Image.PublishedImageEntity;
import Persistence.Entities.UserEntity;
import User.User;

import java.util.*;
import java.util.stream.Collectors;

public class EntityMapper {

    public static User toDomain(UserEntity entity){
        return new User(entity.getId(),entity.getUsername(),entity.getEmail(),entity.getNickname());
    }

    /**
     * Convert a user to a user entity, for persistence. Should be used only when registering a new user
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
    public static SetEntity toEntity(SetModule set, UUID imageId){
        ImageComponentKey key= new ImageComponentKey(imageId,set.getSet().getName());
        return new SetEntity(key,toEntity(set.getSet().getDataType()),set.getSet().getData(), set.getAlias());
    }
    public static SetModule toDomain(SetEntity entity){
        return new SetModule(new ModelSet(entity.getModelDataKey().getName(),
                toDomain(entity.getType()),entity.getData()),entity.getAlias());
    }
    public static ParameterEntity toEntity(ParameterModule parameter, UUID imageId){
        return new ParameterEntity(imageId, parameter.getParameter().getName(), parameter.getParameter().getDataType().toString(), parameter.getParameter().getData(), parameter.getAlias());
    }
    public static ParameterModule toDomain(ParameterEntity entity){
        return new ParameterModule(new ModelParameter(entity.getName(),toDomain(entity.getType()),entity.getData()),entity.getAlias());
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

    public static Set<VariableModule> toVariables (Collection<VariableEntity> entities) {
        return entities.stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toSet());
    }

    public static Set<VariableEntity> toVariableEntities (Collection<VariableModule> variables, UUID imageId) {
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

    public static Set<SetModule> toSets (Collection<SetEntity> entities) {
        return entities.stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toSet());
    }
    public static Set<ParameterModule> toParams (Collection<ParameterEntity> entities) {
        return entities.stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toSet());
    }
    public static Set<SetEntity> toSetEntities (Collection<SetModule> sets, UUID imageId) {
        return sets.stream()
                .map(set -> EntityMapper.toEntity(set, imageId)).collect(Collectors.toSet());
    }
    public static Set<ParameterEntity> toParamEntities (Collection<ParameterModule> params, UUID imageId) {
        return params.stream()
                .map(Param -> EntityMapper.toEntity(Param, imageId)).collect(Collectors.toSet());
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
    public static VariableModule toDomain(VariableEntity entity){
        return new VariableModule(new Variable(entity.getName(),entity.getStructure()),entity.getAlias());
    }
    public static VariableEntity toEntity(VariableModule variable, UUID imageId){
        return new VariableEntity(imageId,variable.getVariable().getName(),variable.getVariable().getStructure(),variable.getAlias());
    }
    public static Image toDomain(ImageEntity imageEntity){
        return new Image(imageEntity.getZimplCode(),toConstraintModules(imageEntity.getConstraintModules()),
                toPreferenceModules(imageEntity.getPreferenceModules()), toSets(imageEntity.getActiveSets()),
                toParams(imageEntity.getActiveParams()), toVariables(imageEntity.getVariables()));
    }
    public static ImageEntity toEntity(UserEntity user,Image image, UUID imageId){
        return new ImageEntity(image.getName(),image.getDescription(),image.getCreationDate(),
                toPreferenceModuleEntities(image.getPreferenceModules().values(),imageId),
                toConstraintModuleEntities(image.getConstraintsModules().values(), imageId),
                toVariableEntities(image.getActiveVariables(),imageId),
                toParamEntities(image.getActiveParams(),imageId),
                toSetEntities(image.getActiveSets(),imageId),
                image.getSourceCode(),user);
    }
    public static void setEntity(ImageEntity existingEntity,UserEntity user,Image image){
        UUID imageId=existingEntity.getId();
        existingEntity.setAll(image.getName(),image.getDescription(),image.getCreationDate(),
                toPreferenceModuleEntities(image.getPreferenceModules().values(),imageId),
                toConstraintModuleEntities(image.getConstraintsModules().values(), imageId),
                toVariableEntities(image.getActiveVariables(),imageId),
                toParamEntities(image.getActiveParams(),imageId),
                toSetEntities(image.getActiveSets(),imageId),
                image.getSourceCode(),user);
    }
    public static PublishedImageEntity publishImage(ImageEntity imageEntity, UUID imageId) {
        return new PublishedImageEntity(
                imageEntity.getName(),
                imageEntity.getDescription(),
                imageEntity.getCreationDate(),
                imageEntity.getPreferenceModules().stream()
                        .map(pref -> new PreferenceModuleEntity(
                                imageId,
                                pref.getName(),
                                pref.getDescription(),
                                new HashSet<>(pref.getPreferences())
                        ))
                        .collect(Collectors.toSet()),
                imageEntity.getConstraintModules().stream()
                        .map(cons -> new ConstraintModuleEntity(
                                imageId,
                                cons.getName(),
                                cons.getDescription(),
                                new HashSet<>(cons.getConstraints())
                        ))
                        .collect(Collectors.toSet()),
                imageEntity.getVariables().stream()
                        .map(var -> new VariableEntity(
                                imageId,
                                var.getName(),
                                var.getStructure(),
                                var.getAlias()
                        ))
                        .collect(Collectors.toSet()),
                imageEntity.getActiveParams().stream()
                        .map(param -> new ParameterEntity(
                                imageId,
                                param.getName(),
                                param.getType(),
                                param.getData(),
                                param.getAlias()
                        ))
                        .collect(Collectors.toSet()),
                imageEntity.getActiveSets().stream()
                        .map(set -> new SetEntity(
                                new ImageComponentKey(imageId, set.getName()),
                                set.getType(),
                                new ArrayList<>(set.getData()),
                                set.getAlias()
                        ))
                        .collect(Collectors.toSet()),
                imageEntity.getOriginal_code(),
                imageEntity.getUser().getUsername()
        );
    }
        public static void setEntity(PublishedImageEntity existingEntity, ImageEntity imageEntity){
            UUID imageId=existingEntity.getId();
            existingEntity.setAll(
                    imageEntity.getName(),
                    imageEntity.getDescription(),
                    imageEntity.getCreationDate(),
                    imageEntity.getPreferenceModules().stream()
                            .map(pref -> new PreferenceModuleEntity(
                                    imageId,
                                    pref.getName(),
                                    pref.getDescription(),
                                    new HashSet<>(pref.getPreferences())
                            ))
                            .collect(Collectors.toSet()),
                    imageEntity.getConstraintModules().stream()
                            .map(cons -> new ConstraintModuleEntity(
                                    imageId,
                                    cons.getName(),
                                    cons.getDescription(),
                                    new HashSet<>(cons.getConstraints())
                            ))
                            .collect(Collectors.toSet()),
                    imageEntity.getVariables().stream()
                            .map(var -> new VariableEntity(
                                    imageId,
                                    var.getName(),
                                    var.getStructure(),
                                    var.getAlias()
                            ))
                            .collect(Collectors.toSet()),
                    imageEntity.getActiveParams().stream()
                            .map(param -> new ParameterEntity(
                                    imageId,
                                    param.getName(),
                                    param.getType(),
                                    param.getData(),
                                    param.getAlias()
                            ))
                            .collect(Collectors.toSet()),
                    imageEntity.getActiveSets().stream()
                            .map(set -> new SetEntity(
                                    new ImageComponentKey(imageId, set.getName()),
                                    set.getType(),
                                    new ArrayList<>(set.getData()),
                                    set.getAlias()
                            ))
                            .collect(Collectors.toSet()),
                    imageEntity.getOriginal_code(),
                    imageEntity.getUser().getUsername()
            );
    }

}
