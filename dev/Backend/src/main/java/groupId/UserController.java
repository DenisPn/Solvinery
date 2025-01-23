package groupId;

import DTO.Factories.RecordFactory;
import DTO.Records.Image.ConstraintModuleDTO;
import DTO.Records.Image.PreferenceModuleDTO;
import DTO.Records.Model.ModelDefinition.VariableDTO;
import DTO.Records.Requests.Commands.SolveCommandDTO;
import DTO.Records.Image.ImageDTO;
import DTO.Records.Image.SolutionDTO;
import DTO.Records.Requests.Responses.CreateImageResponseDTO;
import DTO.Records.Requests.Responses.ImageResponseDTO;
import Image.Image;
import Model.ModelConstraint;
import Model.ModelInterface;
import Model.ModelVariable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserController {
private final Map<UUID,Image> images;


    public UserController(){
        images = new HashMap<>();
    }


    public CreateImageResponseDTO createImageFromFile(String code) throws IOException {
        UUID id= UUID.randomUUID();
        String name = id.toString();
        Path path= Paths.get("User/Models"+ File.separator+name+".zpl");
        Files.createDirectories(path.getParent());
        Files.writeString(path,code, StandardOpenOption.CREATE);
        Image image=new Image(path.toAbsolutePath().toString());
        images.put(id,image);
        return RecordFactory.makeDTO(id,image.getModel());
    }

    public SolutionDTO solve(SolveCommandDTO command) {
        return images.get(UUID.fromString(command.id())).solve(Integer.parseInt(command.timeout()));
    }

    public void overrideImage(ImageDTO imgConfig) {
        Image image=images.get(UUID.fromString(imgConfig.imageId()));
        Objects.requireNonNull(image,"Invalid id in image config/override image");
        Map<String, ModelVariable> variables = new HashMap<>();
        ModelInterface model= image.getModel();
        for(String variable:imgConfig.variablesModule().variablesOfInterest()){
            ModelVariable modelVariable=model.getVariable(variable);
            Objects.requireNonNull(modelVariable,"Invalid variable name in config/override image");
            variables.put(variable,modelVariable);
        }
        image.reset(variables, imgConfig.variablesModule().variablesConfigurableSets(),imgConfig.variablesModule().variablesConfigurableParams());
        for(ConstraintModuleDTO constraintModule:imgConfig.constraintModules()){
            image.addConstraintModule(constraintModule.moduleName(),constraintModule.description(),
                    constraintModule.constraints(),constraintModule.inputSets(),constraintModule.inputParams());
        }
        for (PreferenceModuleDTO preferenceModule:imgConfig.preferenceModules()){
            image.addPreferenceModule(preferenceModule.moduleName(), preferenceModule.description(),
                    preferenceModule.preferences(),preferenceModule.inputSets(),preferenceModule.inputParams());
        }
    }
}
