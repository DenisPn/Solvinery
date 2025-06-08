package Persistence.Entities.Image;

import Persistence.Entities.Image.Data.ParameterEntity;
import Persistence.Entities.Image.Data.SetEntity;
import Persistence.Entities.Image.Data.VariableEntity;
import Persistence.Entities.Image.Operational.ConstraintModuleEntity;
import Persistence.Entities.Image.Operational.PreferenceModuleEntity;
import Persistence.Entities.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "image")
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "imageComponentKey.imageId",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SetEntity> activeSets;

    @OneToMany(mappedBy = "imageComponentKey.imageId",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ParameterEntity> activeParams;

    @OneToMany(mappedBy = "imageComponentKey.imageId",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VariableEntity> variables;

    @OneToMany(mappedBy = "imageComponentKey.imageId",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConstraintModuleEntity> constraintModules;

    @OneToMany(mappedBy = "imageComponentKey.imageId",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PreferenceModuleEntity> preferenceModules;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

   /* @Lob
    @Column(name = "image_zpl_code", nullable = false,
    columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private String zimplCode;*/

    @Lob
    @Column(name = "original_source_code", nullable = false,
            columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private String original_code;

    public ImageEntity () {
        this.activeSets = new HashSet<>();
        this.activeParams = new HashSet<>();
        this.variables = new HashSet<>();
        this.constraintModules = new HashSet<>();
        this.preferenceModules = new HashSet<>();
        //this.zimplCode = "";
        this.original_code = "";
        this.name = "";
        this.description = "";
        this.creationDate = LocalDateTime.now();
        this.user = null;
    }
    public ImageEntity (UserEntity user) {
        this.activeSets = new HashSet<>();
        this.activeParams = new HashSet<>();
        this.variables = new HashSet<>();
        this.constraintModules = new HashSet<>();
        this.preferenceModules = new HashSet<>();
        //this.zimplCode = "";
        this.original_code = "";
        this.name = "";
        this.description = "";
        this.creationDate = LocalDateTime.now();
        this.user = user;
    }
    public ImageEntity (String name,String description,LocalDateTime creationDate,Set<PreferenceModuleEntity> preferenceModules, Set<ConstraintModuleEntity> constraintModules, Set<VariableEntity> variables, Set<ParameterEntity> activeParams, Set<SetEntity> activeSets, String original_code,UserEntity user) {
        this.preferenceModules = preferenceModules;
        this.constraintModules = constraintModules;
        this.variables = variables;
        this.activeParams = activeParams;
        this.activeSets = activeSets;
        //this.zimplCode = zimplCode;
        this.original_code = original_code;
        this.user = user;
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
    }
    @Deprecated(forRemoval = true)
    public ImageEntity (String name,String description,LocalDateTime creationDate,Set<PreferenceModuleEntity> preferenceModules, Set<ConstraintModuleEntity> constraintModules, Set<VariableEntity> variables, Set<ParameterEntity> activeParams, Set<SetEntity> activeSets,UserEntity user) {
        this.preferenceModules = preferenceModules;
        this.constraintModules = constraintModules;
        this.variables = variables;
        this.activeParams = activeParams;
        this.activeSets = activeSets;
        //this.zimplCode = zimplCode;
        this.original_code = "";
        this.user = user;
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
    }

    public UUID getId () {
        return id;
    }
    @Deprecated(forRemoval = true) //use updated one
    public void setAll (Set<PreferenceModuleEntity> preferenceModules, Set<ConstraintModuleEntity> constraintModules, Set<VariableEntity> variables, Set<ParameterEntity> activeParams, Set<SetEntity> activeSets, String zimplCode) {
        this.preferenceModules = preferenceModules;
        this.constraintModules = constraintModules;
        this.variables = variables;
        this.activeParams = activeParams;
        this.activeSets = activeSets;
        //this.zimplCode = zimplCode;
    }
    public void setAll(String name, String description, LocalDateTime creationDate, Set<PreferenceModuleEntity> preferenceModuleEntities, Set<ConstraintModuleEntity> constraintModuleEntities, Set<VariableEntity> variableEntities, Set<ParameterEntity> paramEntities, Set<SetEntity> setEntities, String sourceCode, UserEntity user) {
        this.preferenceModules = preferenceModuleEntities;
        this.constraintModules = constraintModuleEntities;
        this.variables = variableEntities;
        this.activeParams = paramEntities;
        this.activeSets = setEntities;
        //this.zimplCode = sourceCode;
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
        this.user = user;
        this.original_code = sourceCode;
    }


    public UserEntity getUser() {
        return user;
    }

    /*public String getZimplCode () {
        return zimplCode;
    }
    public void setZimplCode (String zimplCode) {
        this.zimplCode = zimplCode;
    }*/
    public Set<SetEntity> getActiveSets () {
        return activeSets;
    }

    public void addSet (SetEntity set) {
        this.activeSets.add(set);
    }

    public void removeSet (SetEntity set) {
        this.activeSets.remove(set);
    }

    public Set<ParameterEntity> getActiveParams () {
        return activeParams;
    }

    public void addParam (ParameterEntity param) {
        this.activeParams.add(param);
    }

    public void removeParam (ParameterEntity param) {
        this.activeParams.remove(param);
    }

    public Set<VariableEntity> getVariables () {
        return variables;
    }

    public void addVariable (VariableEntity variable) {
        this.variables.add(variable);
    }

    public void removeVariable (VariableEntity variable) {
        this.variables.remove(variable);
    }

    public Set<ConstraintModuleEntity> getConstraintModules () {
        return constraintModules;
    }

    public void addConstraintModule (ConstraintModuleEntity constraintModule) {
        this.constraintModules.add(constraintModule);
    }

    public void removeConstraintModule (ConstraintModuleEntity constraintModule) {
        this.constraintModules.remove(constraintModule);
    }

    public Set<PreferenceModuleEntity> getPreferenceModules () {
        return preferenceModules;
    }

    public void addPreferenceModule (PreferenceModuleEntity preferenceModule) {
        this.preferenceModules.add(preferenceModule);
    }

    public void removePreferenceModule (PreferenceModuleEntity preferenceModule) {
        this.preferenceModules.remove(preferenceModule);
    }

    public String getOriginalCode() {
        return original_code;
    }

    public void setOriginal_code(String original_code) {
        this.original_code = original_code;
    }

    public void setActiveSets (Set<SetEntity> activeSets) {
        this.activeSets = activeSets;
    }

    public void setActiveParams (Set<ParameterEntity> activeParams) {
        this.activeParams = activeParams;
    }

    public void setVariables (Set<VariableEntity> variables) {
        this.variables = variables;
    }

    public void setConstraintModules (Set<ConstraintModuleEntity> constraintModules) {
        this.constraintModules = constraintModules;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPreferenceModules (Set<PreferenceModuleEntity> preferenceModules) {
        this.preferenceModules = preferenceModules;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ImageEntity that = (ImageEntity) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(creationDate, that.creationDate)
                && Objects.equals(activeSets, that.activeSets)
                && Objects.equals(activeParams, that.activeParams)
                && Objects.equals(variables, that.variables)
                && Objects.equals(constraintModules, that.constraintModules)
                && Objects.equals(preferenceModules, that.preferenceModules)
                && Objects.equals(user, that.user)
                //&& Objects.equals(zimplCode, that.zimplCode)
                && Objects.equals(original_code, that.original_code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, creationDate, activeSets, activeParams, variables, constraintModules, preferenceModules, user, /*zimplCode,*/ original_code);
    }
}
