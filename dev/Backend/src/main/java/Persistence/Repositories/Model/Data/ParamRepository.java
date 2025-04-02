package Persistence.Repositories.Model.Data;

import Persistence.Entities.Model.Data.ModelDataKeyPair;
import Persistence.Entities.Model.Data.ModelParamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParamRepository extends JpaRepository<ModelParamEntity, ModelDataKeyPair> {
}
