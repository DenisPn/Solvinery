package Persistence.Repositories.Model.Data;

import Persistence.Entities.Model.Data.ModelDataKeyPair;
import Persistence.Entities.Model.Data.ModelSetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SetRepository extends JpaRepository<ModelSetEntity, ModelDataKeyPair> {
}
