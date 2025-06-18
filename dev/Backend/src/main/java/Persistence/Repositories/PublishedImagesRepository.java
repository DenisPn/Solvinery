package Persistence.Repositories;

import Persistence.Entities.Image.PublishedImageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PublishedImagesRepository  extends JpaRepository<PublishedImageEntity, UUID>,
                                                    JpaSpecificationExecutor<PublishedImageEntity>
{
}
