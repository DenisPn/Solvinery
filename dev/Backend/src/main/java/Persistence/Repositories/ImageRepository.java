package Persistence.Repositories;

import Persistence.Entities.Image.ImageEntity;
import Persistence.Entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<ImageEntity, UUID> {

    Page<ImageEntity> findByUser(UserEntity user, Pageable pageable);

}
