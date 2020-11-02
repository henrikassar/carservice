package hen.sar.carservice.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Table
@RequiredArgsConstructor
@Getter
public class CarLock {
	@Id
	private Long id;
	private final String vin;
	private String keyLock; 
}
