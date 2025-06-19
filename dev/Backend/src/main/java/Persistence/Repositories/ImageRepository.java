package Persistence.Repositories;

import Persistence.Entities.Image.ImageEntity;
import Persistence.Entities.Image.PublishedImageEntity;
import Persistence.Entities.UserEntity;
import groupId.DTO.Records.Requests.Responses.ImagesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<ImageEntity, UUID>,
                                        JpaSpecificationExecutor<ImageEntity> {


}
