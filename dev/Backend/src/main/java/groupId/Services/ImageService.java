package groupId.Services;

import Exceptions.InternalErrors.ClientSideError;
import Image.Image;
import Persistence.Entities.Image.ImageEntity;
import Persistence.Entities.UserEntity;
import Persistence.EntityMapper;
import Persistence.Repositories.ImageRepository;
import groupId.DTO.Factories.RecordFactory;
import groupId.DTO.Records.Image.ImageDTO;
import groupId.DTO.Records.Model.ModelDefinition.ModelDTO;
import groupId.DTO.Records.Requests.Commands.ImageConfigDTO;
import groupId.DTO.Records.Requests.Responses.CreateImageResponseDTO;
import groupId.DTO.Records.Requests.Responses.ParseModelResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${app.file.storage-dir}")
    private String storageDir;

    private final ImageRepository imageRepository;

    private final UserService userService;

    @Autowired
    public ImageService (UserService userService, ImageRepository imageRepository){
        this.imageRepository = imageRepository;
        this.userService = userService;
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
        UserEntity user=userService.getUser(userId).orElseThrow(()-> new ClientSideError("User id not found"));
        ImageDTO imageDTO= imgConfig.image();
        ImageEntity imageEntity=imageRepository.findById(UUID.fromString(imageId))
                .orElseThrow(()->new ClientSideError("Invalid image ID during override image"));
        Image image= EntityMapper.toDomain(imageEntity);
        image.override(imageDTO);
        imageRepository.save(EntityMapper.toEntity(imageEntity.getUser(),image,imageEntity.getId()));
    }

    @Transactional
    public CreateImageResponseDTO createImage(ImageDTO imageDTO, String userId)  {
        UserEntity user=userService.getUser(userId).orElseThrow(()-> new ClientSideError("User id not found"));
        Image image= new Image(imageDTO);
        ImageEntity imageEntity = new ImageEntity(
                imageDTO.name(),
                imageDTO.description(),
                LocalDateTime.now(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                imageDTO.code(),
                user
        );

        imageEntity = imageRepository.save(imageEntity);
        UUID generatedId = imageEntity.getId();
        ImageEntity fullEntity = EntityMapper.toEntity(user,image, generatedId);
        imageEntity.setAll(
                fullEntity.getPreferenceModules(),
                fullEntity.getConstraintModules(),
                fullEntity.getVariables(),
                fullEntity.getActiveParams(),
                fullEntity.getActiveSets(),
                fullEntity.getZimplCode()
        );
        imageRepository.save(imageEntity);
        return new CreateImageResponseDTO(generatedId.toString());
    }

    public Image getImage(String id){
        return EntityMapper.toDomain(imageRepository.getReferenceById(UUID.fromString(id)));
    }
}
