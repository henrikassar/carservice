package hen.sar.carservice.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import hen.sar.carservice.entities.CarEntity;
import reactor.core.publisher.Mono;

@Repository
public interface CarEntityRepository extends ReactiveCrudRepository<CarEntity, Long> {
	public Mono<CarEntity> findFirstByVin(String vin);
}
