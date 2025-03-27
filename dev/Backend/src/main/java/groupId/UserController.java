package groupId;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import Model.Data.Elements.Variable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import groupId.DTO.Factories.RecordFactory;
import groupId.DTO.Records.Image.ConstraintModuleDTO;
import groupId.DTO.Records.Image.ImageDTO;
import groupId.DTO.Records.Image.PreferenceModuleDTO;
import groupId.DTO.Records.Image.SolutionDTO;
import groupId.DTO.Records.Model.ModelData.InputDTO;
import groupId.DTO.Records.Requests.Commands.ImageConfigDTO;
import groupId.DTO.Records.Requests.Commands.SolveCommandDTO;
import groupId.DTO.Records.Requests.Responses.CreateImageResponseDTO;
import Exceptions.InternalErrors.BadRequestException;
import Image.Image;
import Model.ModelInterface;
import Model.ModelType;

@Service
public class UserController {
private final Map<UUID,Image> images;

@Value("${app.file.storage-dir}")
private String storageDir;


    public UserController(){
        images = new HashMap<>();
    }

    //Dependency injection - for TESTS only!
    public UserController(String path){
        images = new HashMap<>();
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
            throw new BadRequestException("Could not determine application directory.");
        }

        // Resolve the path relative to the JAR location
        Path storagePath = Paths.get(appDir, storageDir);
        Files.createDirectories(storagePath);
        Path filePath = storagePath.resolve(name + ".zpl");
        Files.writeString(filePath, code, StandardOpenOption.CREATE);
        Image image = new Image(filePath.toAbsolutePath().toString());
        images.put(id, image);

        return RecordFactory.makeDTO(id, image.getModel());
    }

    /**
     * @param command solve command DTO object
     * @return A DTO object with the parsed solution in its fields
     * @see SolutionDTO
     */
    public SolutionDTO solve(SolveCommandDTO command)  {
        //encapsulated in try-catch, since having a method throw Exception is bad practice.
        try {
            Image image = images.get(UUID.fromString(command.imageId()));
            ModelInterface model = image.getModel();
            for (Map.Entry<String, List<List<String>>> set : command.input().setsToValues().entrySet()) {
                List<String> setElements = new LinkedList<>();
                for (List<String> element : set.getValue()) {
                    String tuple = ModelType.convertArrayOfAtoms((element.toArray(new String[0])), model.getSet(set.getKey()).getType());
                    setElements.add(tuple);
                }

                model.setInput(model.getSet(set.getKey()), setElements.toArray(new String[0]));


            }

            for (Map.Entry<String, List<String>> parameter : command.input().paramsToValues().entrySet()) {
                model.setInput(model.getParameter(parameter.getKey()), ModelType.convertArrayOfAtoms(parameter.getValue().toArray(new String[0]), model.getParameter(parameter.getKey()).getType()));


            }

            for (String constraint : command.input().constraintsToggledOff()) {
                model.toggleFunctionality(model.getConstraint(constraint), false);
            }

            for (String preference : command.input().preferencesToggledOff()) {
                model.toggleFunctionality(model.getPreference(preference), false);
            }

            return image.solve(command.timeout());
        }
        //encapsulated in try-catch, since having a method throw Exception is bad practice.
        catch (Exception e) {
            throw new BadRequestException("Error while solving: "+e.getMessage());
        }
    }

    /**
     * Given DTO object representing an image and an id, overrides the image with the associated ID with the image.
     * @param imgConfig DTO object parsed from HTTP JSON request.
     * @throws BadRequestException Throws exception if image ID does not exist in the server.
     */
    public void overrideImage(ImageConfigDTO imgConfig) throws BadRequestException {
        ImageDTO imageDTO= imgConfig.image();
        Image image=images.get(UUID.fromString(imgConfig.imageId()));
        BadRequestException.requireNotNull(image, "Invalid image ID during override image");
        Map<String, Variable> variables = new HashMap<>();
        ModelInterface model= image.getModel();
        for(String variable:imageDTO.variablesModule().variablesOfInterest()){
            Variable modelVariable=model.getVariable(variable);
            Objects.requireNonNull(modelVariable,"Invalid variable name in config/override image");
            variables.put(variable,modelVariable);
        }
        image.reset(variables /*,imageDTO.variablesModule().variablesConfigurableSets(),imageDTO.variablesModule().variablesConfigurableParams()*/,imageDTO.variablesModule().variableAliases());
        for(ConstraintModuleDTO constraintModule:imageDTO.constraintModules()){
            image.addConstraintModule(constraintModule.moduleName(),constraintModule.description(),
                    constraintModule.constraints()/*,constraintModule.inputSets(),constraintModule.inputParams()*/);
        }
        for (PreferenceModuleDTO preferenceModule:imageDTO.preferenceModules()){
            image.addPreferenceModule(preferenceModule.moduleName(), preferenceModule.description(),
                    preferenceModule.preferences(),preferenceModule.inputSets(),preferenceModule.inputParams());
        }
    }

    /**
     * Given ID, returns the image associated with it.
     * Implemented for testing, and should not be used outside that scope.
     * @param id image id
     * @return Image object
     */
    public Image getImage(String id) {
        return images.get(UUID.fromString(id));
    }

    /**
     * @see InputDTO
     */
    public InputDTO loadLastInput(String imageId) throws Exception {
        return images.get(UUID.fromString(imageId)).getInput();
    }

}
