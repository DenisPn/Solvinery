package groupId.Services;

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
import groupId.DTO.Records.Requests.Commands.RegisterDTO;
import jakarta.validation.Valid;
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
import Model.Data.Types.ModelType;

@Service
public class MainService {
private final Map<UUID,Image> images;




    public MainService (){
        images = new HashMap<>();
    }

    //Dependency injection - for TESTS only!
    public MainService (String path){
        images = new HashMap<>();
        this.storageDir = path;
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
     * Given ID, returns the image associated with it.
     * Implemented for testing, and should not be used outside that scope.
     * @param id image id
     * @return Image object
     */
    public Image getImage(String id) {
        return images.get(UUID.fromString(id));
    }

    public void register (@Valid RegisterDTO data) {

    }

    /* *//**
     * @see InputDTO
     *//*
    public InputDTO loadLastInput(String imageId) throws Exception {
        return images.get(UUID.fromString(imageId)).getInput();
    }
*/
}
