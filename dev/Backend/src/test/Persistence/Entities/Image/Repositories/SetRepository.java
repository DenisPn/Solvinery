package Persistence.Entities.Image.Repositories;

import Persistence.Entities.Image.Data.SetEntity;
import Persistence.Entities.Image.ImageComponentKey;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * Should not be used in the main project, made for testing only.
 */
public interface SetRepository extends JpaRepository<SetEntity, ImageComponentKey> {
}
