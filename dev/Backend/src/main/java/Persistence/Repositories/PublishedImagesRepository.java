package Persistence.Repositories;

import Persistence.Entities.Image.ImageEntity;
import Persistence.Entities.Image.PublishedImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PublishedImagesRepository  extends JpaRepository<PublishedImageEntity, UUID> {
}
