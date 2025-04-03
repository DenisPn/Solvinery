package Persistence.Entities.Image;

import Persistence.Entities.Image.Data.ModelParamEntity;
import Persistence.Entities.Image.Data.ModelSetEntity;
import Persistence.Entities.Image.Data.VariableEntity;
import Persistence.Entities.Image.Operational.ConstraintModuleEntity;
import Persistence.Entities.Image.Operational.PreferenceModuleEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "image")
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @OneToMany(mappedBy = "imageComponentKey.imageId",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ModelSetEntity> activeSets;

    @OneToMany(mappedBy = "imageComponentKey.imageId",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ModelParamEntity> activeParams;

    @OneToMany(mappedBy = "imageComponentKey.imageId",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VariableEntity> variables;

    @OneToMany(mappedBy = "imageComponentKey.imageId",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConstraintModuleEntity> constraintModules;

    @OneToMany(mappedBy = "imageComponentKey.imageId",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PreferenceModuleEntity> preferenceModules;

    public ImageEntity () {
        this.activeSets = new HashSet<>();
        this.activeParams = new HashSet<>();
        this.variables = new HashSet<>();
        this.constraintModules = new HashSet<>();
        this.preferenceModules = new HashSet<>();
    }

    public ImageEntity (Set<PreferenceModuleEntity> preferenceModules, Set<ConstraintModuleEntity> constraintModules, Set<VariableEntity> variables, Set<ModelParamEntity> activeParams, Set<ModelSetEntity> activeSets) {
        this.preferenceModules = preferenceModules;
        this.constraintModules = constraintModules;
        this.variables = variables;
        this.activeParams = activeParams;
        this.activeSets = activeSets;
    }

    public UUID getId () {
        return id;
    }
    public void setAll (Set<PreferenceModuleEntity> preferenceModules, Set<ConstraintModuleEntity> constraintModules, Set<VariableEntity> variables, Set<ModelParamEntity> activeParams, Set<ModelSetEntity> activeSets) {
        this.preferenceModules = preferenceModules;
        this.constraintModules = constraintModules;
        this.variables = variables;
        this.activeParams = activeParams;
        this.activeSets = activeSets;
    }
    public Set<ModelSetEntity> getActiveSets () {
        return activeSets;
    }

    public void addSet (ModelSetEntity set) {
        this.activeSets.add(set);
    }

    public void removeSet (ModelSetEntity set) {
        this.activeSets.remove(set);
    }

    public Set<ModelParamEntity> getActiveParams () {
        return activeParams;
    }

    public void addParam (ModelParamEntity param) {
        this.activeParams.add(param);
    }

    public void removeParam (ModelParamEntity param) {
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

    public void setActiveSets (Set<ModelSetEntity> activeSets) {
        this.activeSets = activeSets;
    }

    public void setActiveParams (Set<ModelParamEntity> activeParams) {
        this.activeParams = activeParams;
    }

    public void setVariables (Set<VariableEntity> variables) {
        this.variables = variables;
    }

    public void setConstraintModules (Set<ConstraintModuleEntity> constraintModules) {
        this.constraintModules = constraintModules;
    }

    public void setPreferenceModules (Set<PreferenceModuleEntity> preferenceModules) {
        this.preferenceModules = preferenceModules;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageEntity that = (ImageEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(activeSets, that.activeSets) &&
                Objects.equals(activeParams, that.activeParams) &&
                Objects.equals(variables, that.variables) &&
                Objects.equals(constraintModules, that.constraintModules) &&
                Objects.equals(preferenceModules, that.preferenceModules);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, activeSets, activeParams, variables, constraintModules, preferenceModules);
    }
}
