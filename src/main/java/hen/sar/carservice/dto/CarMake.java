package hen.sar.carservice.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter 
public class CarMake{
    @JsonProperty("Count") 
    private int count;
    @JsonProperty("Message") 
    private String message;
    @JsonProperty("SearchCriteria") 
    private String searchCriteria;
    @JsonProperty("Results") 
    private List<CarMakeModels> results;
}