package hen.sar.carservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CarLockDto {
	private String vin;
	private String keyLock;
}
