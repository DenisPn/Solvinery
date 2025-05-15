package Persistence.Entities.Image;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "published_images")
public class PublishedImageEntity {

    @Id
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private ImageEntity image;

    @Column(name = "publish_date", nullable = false, updatable = false)
    private LocalDateTime publishDate;




}
