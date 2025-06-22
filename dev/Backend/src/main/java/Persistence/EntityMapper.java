package Persistence;

import Image.Image;
import Image.Modules.Grouping.ConstraintModule;
import Image.Modules.Grouping.PreferenceModule;
import Image.Modules.Single.ParameterModule;
import Image.Modules.Single.SetModule;
import Image.Modules.Single.VariableModule;
import Model.Data.Elements.Operational.Constraint;
import Model.Data.Elements.Operational.Preference;
import Model.Data.Types.ModelPrimitives;
import Model.Data.Types.ModelType;
import Model.Data.Types.Tuple;
import Persistence.Entities.Image.Data.ParameterEntity;
import Persistence.Entities.Image.Data.SetEntity;
import Persistence.Entities.Image.Data.VariableEntity;
import Persistence.Entities.Image.ImageComponentKey;
import Persistence.Entities.Image.ImageEntity;
import Persistence.Entities.Image.Operational.ConstraintEntity;
import Persistence.Entities.Image.Operational.ConstraintModuleEntity;
import Persistence.Entities.Image.Operational.PreferenceEntity;
import Persistence.Entities.Image.Operational.PreferenceModuleEntity;
import Persistence.Entities.Image.PublishedImageEntity;
import Persistence.Entities.UserEntity;
import User.User;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class EntityMapper {

    @NonNull
    public static User toDomain(@NonNull UserEntity entity){
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
    @NonNull
    public static UserEntity toEntity(@NonNull User user, @NonNull String rawPassword){
       return new UserEntity(user.getUsername(),user.getEmail(),rawPassword);
    }
    @NonNull
    public static SetEntity toEntity(@NonNull SetModule set, UUID imageId){
        ImageComponentKey key= new ImageComponentKey(imageId,set.getOriginalName());
        return new SetEntity(key,set.getTypeString(),set.getData(), set.getAlias());
    }
    @NonNull
    public static SetModule toDomain(@NonNull SetEntity entity){
        return new SetModule(entity.getName(),entity.getStructure(),entity.getAlias(),entity.getData());
    }
    @NonNull
    public static ParameterEntity toEntity(@NonNull ParameterModule parameter, UUID imageId){
        return new ParameterEntity(imageId, parameter.getOriginalName(), parameter.getTypeString(), parameter.getData(), parameter.getAlias());
    }
    @NonNull
    public static ParameterModule toDomain(@NonNull ParameterEntity entity){
        return new ParameterModule(entity.getName(),entity.getStructure(),entity.getAlias(),entity.getData());
    }
    @NonNull
    public static Constraint toDomain(@NonNull ConstraintEntity entity){
        return new Constraint(entity.getName());
    }
    @NonNull
    public static ConstraintEntity toEntity(@NonNull Constraint constraint){
        return new ConstraintEntity(constraint.getName());
    }

    @NonNull
    public static Set<ConstraintEntity> toConstraintsEntities (@NonNull Collection<Constraint> constraints) {
        return constraints.stream()
                .map(EntityMapper::toEntity)
                .collect(Collectors.toSet());
    }
    @NonNull
    public static Set<Constraint> toConstraints (@NonNull Collection<ConstraintEntity> entities) {
        return entities.stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toSet());
    }
    @NonNull
    public static Set<Preference> toPreferences (@NonNull Collection<PreferenceEntity> entities) {
        return entities.stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toSet());
    }
    @NonNull
    public static Preference toDomain(@NonNull PreferenceEntity entity){
        return new Preference(entity.getName());
    }
    @NonNull
    public static PreferenceEntity toEntity(@NonNull Preference preference){
        return new PreferenceEntity(preference.getName());
    }

    @NonNull
    public static Set<PreferenceEntity> toPreferencesEntities (@NonNull Collection<Preference> preferences) {
        return preferences.stream()
                .map(EntityMapper::toEntity)
                .collect(Collectors.toSet());
    }

    @NonNull
    public static Set<VariableModule> toVariables (@NonNull Collection<VariableEntity> entities) {
        return entities.stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toSet());
    }

    @NonNull
    public static Set<VariableEntity> toVariableEntities (@NonNull Collection<VariableModule> variables, UUID imageId) {
        return variables.stream()
                .map(variable -> EntityMapper.toEntity(variable, imageId))
                .collect(Collectors.toSet());
    }

    @NonNull
    public static Set<ConstraintModuleEntity> toConstraintModuleEntities (@NonNull Collection<ConstraintModule> constraintModules, UUID imageId) {
        return constraintModules.stream()
                .map(variable -> EntityMapper.toEntity(variable, imageId))
                .collect(Collectors.toSet());
    }
    @NonNull
    public static Set<PreferenceModuleEntity> toPreferenceModuleEntities (@NonNull Collection<PreferenceModule> preferenceModules, UUID imageId) {
        return preferenceModules.stream()
                .map(variable -> EntityMapper.toEntity(variable, imageId))
                .collect(Collectors.toSet());
    }

    @NonNull
    public static Set<ConstraintModule> toConstraintModules (@NonNull Collection<ConstraintModuleEntity> entities) {
        return entities.stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toSet());
    }
    @NonNull
    public static Set<PreferenceModule> toPreferenceModules (@NonNull Collection<PreferenceModuleEntity> entities) {
        return entities.stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toSet());
    }

    @NonNull
    public static Set<SetModule> toSets (@NonNull Collection<SetEntity> entities) {
        return entities.stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toSet());
    }
    @NonNull
    public static Set<ParameterModule> toParams (@NonNull Collection<ParameterEntity> entities) {
        return entities.stream()
                .map(EntityMapper::toDomain)
                .collect(Collectors.toSet());
    }
    @NonNull
    public static Set<SetEntity> toSetEntities (@NonNull Collection<SetModule> sets, UUID imageId) {
        return sets.stream()
                .map(set -> EntityMapper.toEntity(set, imageId)).collect(Collectors.toSet());
    }
    @NonNull
    public static Set<ParameterEntity> toParamEntities (@NonNull Collection<ParameterModule> params, UUID imageId) {
        return params.stream()
                .map(Param -> EntityMapper.toEntity(Param, imageId)).collect(Collectors.toSet());
    }
    @NonNull
    public static ConstraintModuleEntity toEntity(@NonNull ConstraintModule module, UUID imageId){
        return new ConstraintModuleEntity(imageId, module.getName(), module.getDescription(),
                module.getConstraints().stream().map(ConstraintEntity::new).collect(Collectors.toSet()));
    }
    @NonNull
    public static ConstraintModule toDomain(@NonNull ConstraintModuleEntity entity){
        return new ConstraintModule(entity.getName(), entity.getDescription(), toConstraints(entity.getConstraints()));
    }
    @NonNull
    public static PreferenceModuleEntity toEntity(@NonNull PreferenceModule module, UUID imageId){
        return new PreferenceModuleEntity(imageId, module.getName(), module.getDescription(), toPreferencesEntities(module.getPreferences().values()));
    }
    @NonNull
    public static PreferenceModule toDomain(@NonNull PreferenceModuleEntity entity){
        return new PreferenceModule(entity.getName(), entity.getDescription(), toPreferences(entity.getPreferences()));
    }
    @NonNull
    public static VariableModule toDomain(@NonNull VariableEntity entity){

        return new VariableModule(entity.getName(),entity.getTypeStructure(),entity.getAlias(),entity.getObjectiveValueAlias());
    }
    @NonNull
    public static VariableEntity toEntity(@NonNull VariableModule variable, UUID imageId){
        return new VariableEntity(imageId,variable.getName(),variable.getAlias(),variable.getTypeStructure(), variable.getObjectiveValueAlias());
    }
    @NonNull
    public static Image toDomain(@NonNull ImageEntity imageEntity){
        return new Image(imageEntity.getOriginalCode(),imageEntity.getName(),imageEntity.getDescription(),imageEntity.getCreationDate(),toConstraintModules(imageEntity.getConstraintModules()),
                toPreferenceModules(imageEntity.getPreferenceModules()), toSets(imageEntity.getActiveSets()),
                toParams(imageEntity.getActiveParams()), toVariables(imageEntity.getVariables()));
    }
    @NonNull
    public static ImageEntity toEntity(UserEntity user, @NonNull Image image, UUID imageId){
        return new ImageEntity(image.getName(),image.getDescription(),image.getCreationDate(),
                toPreferenceModuleEntities(image.getPreferenceModules().values(),imageId),
                toConstraintModuleEntities(image.getConstraintsModules().values(), imageId),
                toVariableEntities(image.getActiveVariables(),imageId),
                toParamEntities(image.getActiveParams(),imageId),
                toSetEntities(image.getActiveSets(),imageId),
                image.getSourceCode(),user);
    }
    public static void setEntity(@NonNull ImageEntity existingEntity, UserEntity user, @NonNull Image image){
        UUID imageId=existingEntity.getId();
        existingEntity.setAll(image.getName(),image.getDescription(),image.getCreationDate(),
                toPreferenceModuleEntities(image.getPreferenceModules().values(),imageId),
                toConstraintModuleEntities(image.getConstraintsModules().values(), imageId),
                toVariableEntities(image.getActiveVariables(),imageId),
                toParamEntities(image.getActiveParams(),imageId),
                toSetEntities(image.getActiveSets(),imageId),
                image.getSourceCode(),user);
    }
    public static void setEntity(@NonNull ImageEntity existingEntity, UserEntity user, @NonNull ImageEntity entity){
        UUID imageId=existingEntity.getId();
        Image image=toDomain(entity);
        existingEntity.setAll(image.getName(),image.getDescription(),image.getCreationDate(),
                toPreferenceModuleEntities(image.getPreferenceModules().values(),imageId),
                toConstraintModuleEntities(image.getConstraintsModules().values(), imageId),
                toVariableEntities(image.getActiveVariables(),imageId),
                toParamEntities(image.getActiveParams(),imageId),
                toSetEntities(image.getActiveSets(),imageId),
                image.getSourceCode(),user);
    }
    @NonNull
    public static PublishedImageEntity publishImage(@NonNull ImageEntity imageEntity, UUID imageId) {
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
                                var.getAlias(),
                                var.getTypeStructure(),
                                var.getObjectiveValueAlias()
                                ))
                        .collect(Collectors.toSet()),
                imageEntity.getActiveParams().stream()
                        .map(param -> new ParameterEntity(
                                imageId,
                                param.getName(),
                                param.getStructure(),
                                param.getData(),
                                param.getAlias()
                        ))
                        .collect(Collectors.toSet()),
                imageEntity.getActiveSets().stream()
                        .map(set -> new SetEntity(
                                new ImageComponentKey(imageId, set.getName()),
                                set.getStructure(),
                                new ArrayList<>(set.getData()),
                                set.getAlias()
                        ))
                        .collect(Collectors.toSet()),
                imageEntity.getOriginalCode(),
                imageEntity.getUser().getUsername()
        );
    }
        public static void setEntity(@NonNull PublishedImageEntity existingEntity, @NonNull UserEntity user, @NonNull ImageEntity toCopy){
            UUID imageId=existingEntity.getId();
            existingEntity.setAll(
                    toCopy.getName(),
                    toCopy.getDescription(),
                    toCopy.getCreationDate(),
                    toCopy.getPreferenceModules().stream()
                            .map(pref -> new PreferenceModuleEntity(
                                    imageId,
                                    pref.getName(),
                                    pref.getDescription(),
                                    new HashSet<>(pref.getPreferences())
                            ))
                            .collect(Collectors.toSet()),
                    toCopy.getConstraintModules().stream()
                            .map(cons -> new ConstraintModuleEntity(
                                    imageId,
                                    cons.getName(),
                                    cons.getDescription(),
                                    new HashSet<>(cons.getConstraints())
                            ))
                            .collect(Collectors.toSet()),
                    toCopy.getVariables().stream()
                            .map(var -> new VariableEntity(
                                    imageId,
                                    var.getName(),
                                    var.getAlias(),
                                    var.getTypeStructure(),
                                    var.getObjectiveValueAlias()
                            ))
                            .collect(Collectors.toSet()),
                    toCopy.getActiveParams().stream()
                            .map(param -> new ParameterEntity(
                                    imageId,
                                    param.getName(),
                                    param.getStructure(),
                                    param.getData(),
                                    param.getAlias()
                            ))
                            .collect(Collectors.toSet()),
                    toCopy.getActiveSets().stream()
                            .map(set -> new SetEntity(
                                    new ImageComponentKey(imageId, set.getName()),
                                    set.getStructure(),
                                    new ArrayList<>(set.getData()),
                                    set.getAlias()
                            ))
                            .collect(Collectors.toSet()),
                    toCopy.getOriginalCode(),
                    user.getNickname()
            );
    }
    public static void setEntity(@NonNull ImageEntity existingEntity, UserEntity user, @NonNull PublishedImageEntity toCopy){
        UUID imageId=existingEntity.getId();
        existingEntity.setAll(
                toCopy.getName(),
                toCopy.getDescription(),
                toCopy.getCreationDate(),
                toCopy.getPreferenceModules().stream()
                        .map(pref -> new PreferenceModuleEntity(
                                imageId,
                                pref.getName(),
                                pref.getDescription(),
                                new HashSet<>(pref.getPreferences())
                        ))
                        .collect(Collectors.toSet()),
                toCopy.getConstraintModules().stream()
                        .map(cons -> new ConstraintModuleEntity(
                                imageId,
                                cons.getName(),
                                cons.getDescription(),
                                new HashSet<>(cons.getConstraints())
                        ))
                        .collect(Collectors.toSet()),
                toCopy.getVariables().stream()
                        .map(var -> new VariableEntity(
                                imageId,
                                var.getName(),
                                var.getAlias(),
                                var.getTypeStructure(),
                                var.getObjectiveValueAlias()
                        ))
                        .collect(Collectors.toSet()),
                toCopy.getActiveParams().stream()
                        .map(param -> new ParameterEntity(
                                imageId,
                                param.getName(),
                                param.getStructure(),
                                param.getData(),
                                param.getAlias()
                        ))
                        .collect(Collectors.toSet()),
                toCopy.getActiveSets().stream()
                        .map(set -> new SetEntity(
                                new ImageComponentKey(imageId, set.getName()),
                                set.getStructure(),
                                new ArrayList<>(set.getData()),
                                set.getAlias()
                        ))
                        .collect(Collectors.toSet()),
                toCopy.getOriginal_code(),
                user
        );
    }

}
