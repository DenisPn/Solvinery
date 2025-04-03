package Persistence.Entities.Image.Repositories;

import Persistence.Entities.Image.ImageComponentKey;
import Persistence.Entities.Image.Data.ModelSetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SetRepository extends JpaRepository<ModelSetEntity, ImageComponentKey> {
}
