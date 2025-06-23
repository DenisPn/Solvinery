package groupId.DTO.Records.Image;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.Set;


/**
 *
 * @param moduleName the module's name
 * @param description the module's description
 * @param preferences a list of preference names which are in the module
 */
public record PreferenceModuleDTO(@NotBlank @Size(max = 255, message = "Preference module name has be shorter then 4000 characters") String moduleName,
                                  @NotNull  @Size(max = 4000, message = "Preference module description has be shorter then 255 characters") String description,
                                  @Size(min = 1, message = "Preference module  has be contain at least one preference then 4000 characters" )
                                  @NotNull @Valid Set<@Size(max = 255, message = "Preference name has be shorter then 4000 characters") @NotBlank String> preferences,
                                  @Min(0) @Max(1) Float scalar
                                ) {}
