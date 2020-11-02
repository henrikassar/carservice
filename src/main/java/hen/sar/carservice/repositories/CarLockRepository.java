package hen.sar.carservice.repositories;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import hen.sar.carservice.entities.CarLock;
import reactor.core.publisher.Mono;

@Repository
public interface CarLockRepository extends ReactiveCrudRepository<CarLock, Long> {
	public Mono<CarLock> findFirstByVin(String vin);
	
	@Modifying
	@Query("UPDATE Car_Lock SET key_Lock = null where vin=:vin and key_Lock=:keyLock ")
	Mono<Integer> unlockCar(String vin, String keyLock);

	@Modifying
	@Query("UPDATE Car_Lock SET key_Lock = :keyLock where vin=:vin and key_Lock is null")
	Mono<Integer> lockCar(String vin, String keyLock);
	
	public static boolean isSuccess( int  rowCount ) { return rowCount > 0; }
}
