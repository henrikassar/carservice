package hen.sar.carservice.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
public class CarDto {
	@NotBlank
	private String vin;
	@NotBlank
	private String make;
	@NotBlank
	private String model;
	private String plateNumber;
}
