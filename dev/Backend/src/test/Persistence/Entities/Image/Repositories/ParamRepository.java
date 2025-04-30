package Persistence.Entities.Image.Repositories;

import Persistence.Entities.Image.ImageComponentKey;
import Persistence.Entities.Image.Data.ParameterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Should not be used in the main project, made for testing only.
 */
public interface ParamRepository extends JpaRepository<ParameterEntity, ImageComponentKey> {
}
