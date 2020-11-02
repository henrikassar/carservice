package hen.sar.carservice.mappers;

import hen.sar.carservice.dto.CarDto;
import hen.sar.carservice.entities.CarEntity;

public class CarServiceMappers {
	
	public static CarEntity toCarEntity(CarDto t) {
		return new CarEntity(null,t.getVin(),t.getMake(),t.getModel(),t.getPlateNumber());
	}

	public static CarDto toCarDto(CarEntity t) {
		return CarDto
				.builder()
				.vin(t.getVin())
				.make(t.getMake())
				.model(t.getModel())
				.plateNumber(t.getPlateNumber())
				.build();
	}
	
}
