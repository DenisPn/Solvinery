package groupId.Services;

import Exceptions.InternalErrors.ClientSideError;
import Image.Image;
import Model.Model;
import Persistence.Entities.Image.ImageEntity;
import Persistence.Entities.Image.PublishedImageEntity;
import Persistence.Entities.UserEntity;
import Persistence.EntityMapper;
import Persistence.Repositories.ImageRepository;
import Persistence.Repositories.PublishedImagesRepository;
import groupId.DTO.Factories.RecordFactory;
import groupId.DTO.Records.Image.ImageDTO;
import groupId.DTO.Records.Model.ModelDefinition.ModelDTO;
import groupId.DTO.Records.Requests.Responses.*;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
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
        /*UserEntity user= userService.getUser(userId).orElseThrow(()-> new ClientSideError("User id not found"));
        Image image = new Image(code);
        ImageEntity imageEntity= EntityMapper.toEntity(user,image, null);
        imageEntity = imageRepository.save(imageEntity);
        Objects.requireNonNull(imageEntity,"ImageEntity from EntityMapper is null while creating new image");
        UUID generatedId = imageEntity.getId();
        return RecordFactory.makeDTO(generatedId, image.getModel());*/
        return null;
    }
    @Transactional(readOnly = true)
    public ModelDTO parseImage(String code, String userId)  {
        userService.getUser(userId).orElseThrow(() -> new ClientSideError("User id not found"));
        Model model= new Model(code);
        //Image image = new Image(code);
        return RecordFactory.makeDTO(model);
    }
    /**
     * Given DTO object representing an image and an id, overrides the image with the associated ID with the image.
     * @param imgConfig DTO object parsed from HTTP JSON request.
     */
    @Transactional
    public void overrideImage(String userId,String imageId,ImageDTO imageDTO) {
        UserEntity user=userService.getUser(userId).orElseThrow(()-> new ClientSideError("User ID not found"));
        //ImageDTO imageDTO= imgConfig.image();
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
        imageRepository.save(imageEntity);
        return new CreateImageResponseDTO(imageEntity.getId().toString());
    }


    /**
     * Fetches a page of published images from the database.
     * @param pageNumber the page number to fetch
     * @return a DTO object containing a list of image data DTOs and the total number of pages in the database.
     * if the page doesn't exist, returns an empty DTO. is the page is the last one, returns a possibly partially filled DTO.
     * @see PublishedImagesDTO
     */
    @Transactional(readOnly = true)
    public PublishedImagesDTO fetchPublishedImages(@Min(0) int pageNumber) {

        PageRequest pageRequest = PageRequest.of(
                pageNumber,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "publishDate")
        );

        Page<PublishedImageEntity> imagesPage = publishedImagesRepository.findAll(pageRequest);
        Map<UUID,ImageDataDTO> imageDTOs = imagesPage.getContent().stream()
                .collect(Collectors.toMap(PublishedImageEntity::getId,image -> new ImageDataDTO(
                        image.getName(),
                        image.getDescription(),
                        image.getCreationDate(),
                        image.getAuthor())));

        return new PublishedImagesDTO(imageDTOs);
    }
    @Transactional
    public void publishImage(String userId, String imageId) {
        UserEntity user=userService.getUser(userId).orElseThrow(()-> new ClientSideError("Invalid User id during publish image."));
        ImageEntity imageEntity=imageRepository.findById(UUID.fromString(imageId))
                .orElseThrow(()->new ClientSideError("Invalid image ID during publish image."));
        if(!imageEntity.getUser().equals(user))
            throw new ClientSideError("User does not own the image to publish.");
        PublishedImageEntity publishedImageEntity= new PublishedImageEntity();
        publishedImageEntity = publishedImagesRepository.save(publishedImageEntity);
        EntityMapper.setEntity(publishedImageEntity,user,imageEntity);
        publishedImagesRepository.save(publishedImageEntity);
    }
    @Transactional(readOnly = true)
    public ImagesDTO fetchUserImages(@Min(0) int pageNumber, String userId) {
        UserEntity user = userService.getUser(userId)
                .orElseThrow(() -> new ClientSideError("User id not found"));

        Page<ImageEntity> userImages = imageRepository.findByUser(user, PageRequest.of(pageNumber, PAGE_SIZE) );

        Map<UUID,ImageDTO> imageDTOs = userImages.stream()
                .collect(Collectors.toMap(
                        ImageEntity::getId,
                        image -> RecordFactory.makeDTO(EntityMapper.toDomain(image))
                ));
        return new ImagesDTO(imageDTOs);
    }
    @Transactional
    public CreateImageResponseDTO addPublishedImage(String userId, String imageId) {
        UserEntity user = userService.getUser(userId)
                .orElseThrow(() -> new ClientSideError("User id not found"));
        PublishedImageEntity publishedImageEntity = publishedImagesRepository.findById(UUID.fromString(imageId))
                .orElseThrow(()->new ClientSideError("Invalid image ID: image not published."));
        ImageEntity imageEntity = new ImageEntity(user);
        imageEntity = imageRepository.save(imageEntity);
        EntityMapper.setEntity(imageEntity,user,publishedImageEntity);
        imageRepository.save(imageEntity);
        return new CreateImageResponseDTO(imageEntity.getId().toString());
    }
    @Transactional(readOnly = true)
    public Optional<ImageEntity> getImage(String imageId) {
        return imageRepository.findById(UUID.fromString(imageId));
    }
}
