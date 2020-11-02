package hen.sar.carservice.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class CarEntity {
	@Id
	Long id;
	String vin;
	String make;
	String model;
	String plateNumber;
}
