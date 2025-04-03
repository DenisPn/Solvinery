package Persistence.Entities.Image.Repositories;

import Persistence.Entities.Image.ImageComponentKey;
import Persistence.Entities.Image.Data.ModelParamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParamRepository extends JpaRepository<ModelParamEntity, ImageComponentKey> {
}
