package hen.sar.carservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class CarMakeModels{
    @JsonProperty("MakeID") 
    private int makeID;
    @JsonProperty("Make_Name") 
    private String makeName;
    @JsonProperty("Model_ID") 
    private int modelID;
    @JsonProperty("Model_Name") 
    private String modelName;
}