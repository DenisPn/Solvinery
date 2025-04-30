package groupId.Services;

import Exceptions.InternalErrors.BadRequestException;
import Exceptions.InternalErrors.ImageExceptions.ImageException;
import Image.Image;
import Persistence.Entities.Image.ImageEntity;
import Persistence.EntityMapper;
import groupId.DTO.Factories.RecordFactory;
import groupId.DTO.Records.Requests.Responses.CreateImageResponseDTO;
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
import java.util.UUID;

@Service
public class ImageService {

    @Value("${app.file.storage-dir}")
    private String storageDir;

    public ImageService (){}

    /**
     * For use in tests only.
     * @param path file storage path
     */
    public ImageService (String path){
        this.storageDir = path;
    }





    /**
     * @param code string of zimpl code. has to be valid and compile
     * @return a new DTO of the new image
     * @throws IOException in case any IO errors happen during execution
     * @see CreateImageResponseDTO
     */
    public CreateImageResponseDTO createImageFromFile(String code) throws IOException {
        UUID id = UUID.randomUUID();
        String name = id.toString();

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
        Path filePath = storagePath.resolve(name + ".zpl");
        Files.writeString(filePath, code, StandardOpenOption.CREATE);
        Image image = new Image(filePath.toAbsolutePath().toString());
        ImageEntity imageEntity= EntityMapper.toEntity(image);
        return RecordFactory.makeDTO(id, image.getModel());
    }
}
