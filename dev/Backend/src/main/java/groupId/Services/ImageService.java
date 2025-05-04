package groupId.Services;

import Exceptions.InternalErrors.BadRequestException;
import Exceptions.InternalErrors.ImageExceptions.ImageException;
import Image.Image;
import Model.Data.Elements.Variable;
import Model.ModelInterface;
import Persistence.Entities.Image.ImageEntity;
import Persistence.EntityMapper;
import Persistence.Repositories.ImageRepository;
import groupId.DTO.Factories.RecordFactory;
import groupId.DTO.Records.Image.ConstraintModuleDTO;
import groupId.DTO.Records.Image.ImageDTO;
import groupId.DTO.Records.Image.PreferenceModuleDTO;
import groupId.DTO.Records.Requests.Commands.ImageConfigDTO;
import groupId.DTO.Records.Requests.Responses.CreateImageResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${app.file.storage-dir}")
    private String storageDir;

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService (ImageRepository imageRepository){
        this.imageRepository = imageRepository;
    }

    /**
     * For use in tests only.
     * @param path file storage path
     */
    public ImageService (String path){
        this.storageDir = path;
        imageRepository = null; //should be mocked/used elsewhere, tests don't use the core database
    }





    /**
     * @param code string of zimpl code. has to be valid and compile
     * @return a new DTO of the new image
     * @throws IOException in case any IO errors happen during execution
     * @see CreateImageResponseDTO
     */
    public CreateImageResponseDTO createImageFromFile(String code) throws IOException {
        UUID tmpId = UUID.randomUUID();
        String tmpName = tmpId.toString();

        // Get application directory
        String appDir;
        try {
            URI uri = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            appDir = new File(uri).getParent();
        } catch (Exception e) {
            appDir = System.getProperty("/tmp"); // Fallback
        }

        if (appDir == null) {
            throw new ImageException("Could not determine application directory.");
        }

        // Resolve the path relative to the JAR location
        Path storagePath = Paths.get(appDir, storageDir);
        Files.createDirectories(storagePath);
        Path filePath = storagePath.resolve(tmpName + ".zpl");
        Files.writeString(filePath, code, StandardOpenOption.CREATE);

        // Create a new image, model is created as well.
        // The code is parsed and image validity is verified at this point.
        //persist image
        Image image = new Image(filePath.toAbsolutePath().toString());
        ImageEntity imageEntity= EntityMapper.toEntity(image, null);
        assert imageRepository != null;
        imageEntity = imageRepository.save(imageEntity);
        Objects.requireNonNull(imageEntity,"ImageEntity from EntityMapper is null while creating new image");
        UUID generatedId = imageEntity.getId();
        return RecordFactory.makeDTO(generatedId, image.getModel());
    }
    /**
     * Given DTO object representing an image and an id, overrides the image with the associated ID with the image.
     * @param imgConfig DTO object parsed from HTTP JSON request.
     * @throws BadRequestException Throws exception if image ID does not exist in the server.
     */
    public void overrideImage(ImageConfigDTO imgConfig) throws BadRequestException {
        ImageDTO imageDTO= imgConfig.image();
        ImageEntity imageEntity=imageRepository.findById(UUID.fromString(imgConfig.imageId()))
                .orElseThrow(()->new BadRequestException("Invalid image ID during override image"));
        Image image= EntityMapper.toDomain(imageEntity);
        image.override(imageDTO);
        imageRepository.save(EntityMapper.toEntity(image,imageEntity.getId()));
       /*
        BadRequestException.requireNotNull(image, "Invalid image ID during override image");
        Map<String, Variable> variables = new HashMap<>();
        ModelInterface model= image.getModel();
        for(String variable:imageDTO.variablesModule().variablesOfInterest()){
            Variable modelVariable=model.getVariable(variable);
            Objects.requireNonNull(modelVariable,"Invalid variable name in config/override image");
            variables.put(variable,modelVariable);
        }
        image.reset(variables *//*,imageDTO.variablesModule().variablesConfigurableSets(),imageDTO.variablesModule().variablesConfigurableParams()*//*,imageDTO.variablesModule().variableAliases());
        for(ConstraintModuleDTO constraintModule:imageDTO.constraintModules()){
            image.addConstraintModule(constraintModule.moduleName(),constraintModule.description(),
                    constraintModule.constraints()*//*,constraintModule.inputSets(),constraintModule.inputParams()*//*);
        }
        for (PreferenceModuleDTO preferenceModule:imageDTO.preferenceModules()){
            image.addPreferenceModule(preferenceModule.moduleName(), preferenceModule.description(),
                    preferenceModule.preferences());
        } */
    }
}
