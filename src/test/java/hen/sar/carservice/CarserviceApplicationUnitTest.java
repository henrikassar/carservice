package hen.sar.carservice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import hen.sar.carservice.dto.CarDto;
import hen.sar.carservice.entities.CarEntity;
import hen.sar.carservice.mappers.CarServiceMappers;

public class CarserviceApplicationUnitTest {
	
	private static final String VIN="0781298";
	private static final String MAKE="AUDI";
	private static final String MODEL="A6";
	private static final String PLATE="LLL-000";

    @Test
    void mapCarDtoToCarEntityTest() {
        final CarDto dto = CarDto.builder().vin(VIN).make(MAKE).model(MODEL).plateNumber(PLATE).build();
        final CarEntity car = CarServiceMappers.toCarEntity(dto);
        assertEquals( car.getVin(), dto.getVin());
        assertEquals( car.getMake(), dto.getMake());
        assertEquals( car.getModel(), dto.getModel());
        assertEquals( car.getPlateNumber(), dto.getPlateNumber());
    }

    @Test
    void mapCarEntotyToCarDtoTest() {
    	final CarEntity car = new CarEntity(null,VIN,MAKE,MODEL,PLATE);
    	final CarDto dto = CarServiceMappers.toCarDto(car);
        assertEquals( dto.getVin(), car.getVin());
        assertEquals( dto.getMake(), car.getMake());
        assertEquals( dto.getModel(), car.getModel());
        assertEquals( dto.getPlateNumber(), car.getPlateNumber());
    }
    
}
