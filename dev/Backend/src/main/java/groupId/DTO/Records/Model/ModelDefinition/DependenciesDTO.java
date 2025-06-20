package groupId.DTO.Records.Model.ModelDefinition;


/**
 * The dependencies of a zimpl object, meaning all the data it uses anywhere in its definition (ergo, all the data it depends on)
 * @param setDependencies all the set dependencies
 * @param paramDependencies all the parameter dependencies
 * @see ConstraintDTO
 * @see PreferenceDTO
 * @see VariableDTO
 */
@Deprecated
public record DependenciesDTO(
//    @NotNull @Valid Set<@NotNull String> setDependencies,
//    @NotNull @Valid Set<@NotNull String> paramDependencies
) {}
