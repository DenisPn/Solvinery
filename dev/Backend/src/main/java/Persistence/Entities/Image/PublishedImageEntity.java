package Persistence.Entities.Image;

import Persistence.Entities.Image.Data.ParameterEntity;
import Persistence.Entities.Image.Data.SetEntity;
import Persistence.Entities.Image.Data.VariableEntity;
import Persistence.Entities.Image.Operational.ConstraintModuleEntity;
import Persistence.Entities.Image.Operational.PreferenceModuleEntity;
import jakarta.persistence.*;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "published_images")
public class PublishedImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "imageComponentKey.imageId",
            cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<SetEntity> activeSets;

    @OneToMany(mappedBy = "imageComponentKey.imageId",
            cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<ParameterEntity> activeParams;

    @OneToMany(mappedBy = "imageComponentKey.imageId",
            cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<VariableEntity> variables;

    @OneToMany(mappedBy = "imageComponentKey.imageId",
            cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<ConstraintModuleEntity> constraintModules;

    @OneToMany(mappedBy = "imageComponentKey.imageId",
            cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<PreferenceModuleEntity> preferenceModules;

    @Lob
    @Column(name = "original_source_code", nullable = false,
            columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private String original_code;

    @Column(name = "publish_date", nullable = false, updatable = false)
    private LocalDateTime publishDate;

    @Column(name = "author", nullable = false)
    private String author;



    public PublishedImageEntity () {
        this.activeSets = new HashSet<>();
        this.activeParams = new HashSet<>();
        this.variables = new HashSet<>();
        this.constraintModules = new HashSet<>();
        this.preferenceModules = new HashSet<>();
        this.original_code = "";
        this.author = "";
        this.name = "";
        this.description = "";
        this.publishDate = LocalDateTime.now();
        this.creationDate = LocalDateTime.now();
    }

    public PublishedImageEntity (String name, String description, LocalDateTime creationDate, Set<PreferenceModuleEntity> preferenceModules, Set<ConstraintModuleEntity> constraintModules, Set<VariableEntity> variables, Set<ParameterEntity> activeParams, Set<SetEntity> activeSets, String originalCode,String author) {
        this.preferenceModules = preferenceModules;
        this.constraintModules = constraintModules;
        this.variables = variables;
        this.activeParams = activeParams;
        this.activeSets = activeSets;
        this.original_code = originalCode;
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
        this.publishDate = LocalDateTime.now();
        this.author = author;
    }

    public void setAll(String name, String description, LocalDateTime creationDate, Set<PreferenceModuleEntity> preferenceModuleEntities, Set<ConstraintModuleEntity> constraintModuleEntities, Set<VariableEntity> variableEntities, Set<ParameterEntity> parameterEntities, Set<SetEntity> setEntities, String originalCode, String author) {
        this.preferenceModules = preferenceModuleEntities;
        this.constraintModules = constraintModuleEntities;
        this.variables = variableEntities;
        this.activeParams = parameterEntities;
        this.activeSets = setEntities;
        this.original_code = originalCode;
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
        this.publishDate = LocalDateTime.now();
        this.author = author;
    }

    public void setPreferenceModules(Set<PreferenceModuleEntity> preferenceModules) {
        this.preferenceModules = preferenceModules;
    }

    public void setConstraintModules(Set<ConstraintModuleEntity> constraintModules) {
        this.constraintModules = constraintModules;
    }

    public void setVariables(Set<VariableEntity> variables) {
        this.variables = variables;
    }

    public void setActiveParams(Set<ParameterEntity> activeParams) {
        this.activeParams = activeParams;
    }

    public void setActiveSets(Set<SetEntity> activeSets) {
        this.activeSets = activeSets;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public UUID getId() {
        return id;
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

    public Set<SetEntity> getActiveSets() {
        return activeSets;
    }

    public Set<ParameterEntity> getActiveParams() {
        return activeParams;
    }

    public Set<VariableEntity> getVariables() {
        return variables;
    }

    public Set<ConstraintModuleEntity> getConstraintModules() {
        return constraintModules;
    }

    public Set<PreferenceModuleEntity> getPreferenceModules() {
        return preferenceModules;
    }

    public String getOriginal_code() {
        return original_code;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PublishedImageEntity that = (PublishedImageEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(creationDate, that.creationDate) && Objects.equals(activeSets, that.activeSets) && Objects.equals(activeParams, that.activeParams) && Objects.equals(variables, that.variables) && Objects.equals(constraintModules, that.constraintModules) && Objects.equals(preferenceModules, that.preferenceModules) && Objects.equals(original_code, that.original_code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, creationDate, activeSets, activeParams, variables, constraintModules, preferenceModules, original_code);
    }
}
