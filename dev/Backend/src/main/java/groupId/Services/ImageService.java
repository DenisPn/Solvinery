package groupId.Services;

import Exceptions.InternalErrors.ClientSideError;
import Image.Image;
import Persistence.Entities.Image.ImageEntity;
import Persistence.Entities.Image.PublishedImageEntity;
import Persistence.Entities.UserEntity;
import Persistence.EntityMapper;
import Persistence.Repositories.ImageRepository;
import Persistence.Repositories.PublishedImagesRepository;
import groupId.DTO.Factories.RecordFactory;
import groupId.DTO.Records.Image.ImageDTO;
import groupId.DTO.Records.Model.ModelDefinition.ModelDTO;
import groupId.DTO.Records.Requests.Commands.ImageConfigDTO;
import groupId.DTO.Records.Requests.Responses.CreateImageResponseDTO;
import groupId.DTO.Records.Requests.Responses.ImageDataDTO;
import groupId.DTO.Records.Requests.Responses.ParseModelResponseDTO;
import groupId.DTO.Records.Requests.Responses.PublishedImagesDTO;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ImageService {
    private final static int PAGE_SIZE = 10;

    @Value("${app.file.storage-dir}")
    private String storageDir;

    private final ImageRepository imageRepository;

    private final UserService userService;

    private final PublishedImagesRepository publishedImagesRepository;
    @Autowired
    public ImageService (UserService userService, ImageRepository imageRepository, PublishedImagesRepository publishedImagesRepository){
        this.imageRepository = imageRepository;
        this.userService = userService;
        this.publishedImagesRepository = publishedImagesRepository;
    }



    /**
     * @param code string of zimpl code.
     * @return a new DTO of the new image
     * @see ParseModelResponseDTO
     */
    @Deprecated(forRemoval = true)
    public ParseModelResponseDTO createImageFromCode(String code, String userId)  {
        UserEntity user= userService.getUser(userId).orElseThrow(()-> new ClientSideError("User id not found"));
        Image image = new Image(code);
        ImageEntity imageEntity= EntityMapper.toEntity(user,image, null);
        imageEntity = imageRepository.save(imageEntity);
        Objects.requireNonNull(imageEntity,"ImageEntity from EntityMapper is null while creating new image");
        UUID generatedId = imageEntity.getId();
        return RecordFactory.makeDTO(generatedId, image.getModel());
    }
    public ModelDTO parseImage(String code, String userId)  {
        userService.getUser(userId).orElseThrow(() -> new ClientSideError("User id not found"));
        Image image = new Image(code);
        return RecordFactory.makeDTO(image.getModel());
    }
    /**
     * Given DTO object representing an image and an id, overrides the image with the associated ID with the image.
     * @param imgConfig DTO object parsed from HTTP JSON request.
     */
    @Transactional
    public void overrideImage(String userId,String imageId,ImageConfigDTO imgConfig) {
        UserEntity user=userService.getUser(userId).orElseThrow(()-> new ClientSideError("User ID not found"));
        ImageDTO imageDTO= imgConfig.image();
        ImageEntity imageEntity=imageRepository.findById(UUID.fromString(imageId))
                .orElseThrow(()->new ClientSideError("Invalid image ID during override image"));
        if(!imageEntity.getUser().equals(user))
            throw new ClientSideError("User does not own the image to override.");
        Image image= EntityMapper.toDomain(imageEntity);
        image.override(imageDTO);
        imageRepository.save(EntityMapper.toEntity(imageEntity.getUser(),image,imageEntity.getId()));
    }

    @Transactional
    public CreateImageResponseDTO createImage(ImageDTO imageDTO, String userId)  {
        UserEntity user=userService.getUser(userId).orElseThrow(()-> new ClientSideError("User id not found"));
        Image image= new Image(imageDTO);
        ImageEntity imageEntity = new ImageEntity(user);
        imageEntity = imageRepository.save(imageEntity);
        EntityMapper.setEntity(imageEntity,user,image);
        /*ImageEntity fullEntity = EntityMapper.toEntity(user,image, generatedId);
        imageEntity.setAll(
                fullEntity.getPreferenceModules(),
                fullEntity.getConstraintModules(),
                fullEntity.getVariables(),
                fullEntity.getActiveParams(),
                fullEntity.getActiveSets(),
                fullEntity.getZimplCode()
        );*/
        imageRepository.save(imageEntity);
        return new CreateImageResponseDTO(imageEntity.getId().toString());
    }

    public Image getImage(String id){
        return EntityMapper.toDomain(imageRepository.getReferenceById(UUID.fromString(id)));
    }

    /**
     * Fetches a page of published images from the database.
     * @param pageNumber the page number to fetch
     * @return a DTO object containing a list of image data DTOs and the total number of pages in the database.
     * if the page doesn't exist, returns an empty DTO. is the page is the last one, returns a possibly partially filled DTO.
     * @see PublishedImagesDTO
     */
    public PublishedImagesDTO fetchPublishedImages(@Min(0) int pageNumber) {

        PageRequest pageRequest = PageRequest.of(
                pageNumber,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "publishDate")
        );

        Page<PublishedImageEntity> imagesPage = publishedImagesRepository.findAll(pageRequest);
        Set<ImageDataDTO> imageDTOs = imagesPage.getContent().stream()
                .map(image -> new ImageDataDTO(
                        image.getName(),
                        image.getDescription(),
                        image.getCreationDate(),
                        image.getAuthor()
                ))
                .collect(Collectors.toSet());

        return new PublishedImagesDTO(imageDTOs);
    }

    public void publishImage(String userId, String imageId) {
        UserEntity user=userService.getUser(userId).orElseThrow(()-> new ClientSideError("Invalid User id during publish image."));
        ImageEntity imageEntity=imageRepository.findById(UUID.fromString(imageId))
                .orElseThrow(()->new ClientSideError("Invalid image ID during publish image."));
        if(!imageEntity.getUser().equals(user))
            throw new ClientSideError("User does not own the image to publish.");
        PublishedImageEntity publishedImageEntity= new PublishedImageEntity();
        publishedImageEntity = publishedImagesRepository.save(publishedImageEntity);
        EntityMapper.setEntity(publishedImageEntity,imageEntity);
        publishedImagesRepository.save(publishedImageEntity);
    }
}
