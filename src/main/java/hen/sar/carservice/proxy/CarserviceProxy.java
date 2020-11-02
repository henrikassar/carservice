package hen.sar.carservice.proxy;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebInputException;

import hen.sar.carservice.dto.CarMake;
import hen.sar.carservice.entities.CarEntity;
import hen.sar.carservice.entities.CarLock;
import hen.sar.carservice.repositories.CarEntityRepository;
import hen.sar.carservice.repositories.CarLockRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CarserviceProxy {
	private final CarEntityRepository carRepository;
	private final CarLockRepository carLockRepository;
	private WebClient client = WebClient.create("https://vpic.nhtsa.dot.gov/api");

	public  Mono<CarMake> validateOnPublicApi(CarEntity car) {
	    return 
    		client.get()
            .uri("/vehicles/getmodelsformake/{make}?format=json",car.getMake()).accept(APPLICATION_JSON)
            .retrieve()
            .bodyToMono(CarMake.class)
            .filter(t -> t.getCount() != 0  )
            .filter(t -> t.getResults().stream().anyMatch(model -> car.getModel().equalsIgnoreCase(model.getModelName())))
            .log();
	}
	
	public Flux<CarEntity> findAll() {
		return carRepository.findAll();
	}

	public Mono<CarEntity> findCarByVin(String vin) {
		return carRepository.findFirstByVin(vin);
	}
	
	@Transactional
	public Mono<CarEntity> createCar(CarEntity car) {
		return findCarByVin(car.getVin())
				.switchIfEmpty(Mono.zip(carRepository.save(car), 
						                carLockRepository.save(new CarLock(car.getVin())))
							   .flatMap(tuple -> Mono.just(tuple.getT1())));
	}

	public Mono<String> getCarCurrentLockState(String vin) {
		return carLockRepository
				.findFirstByVin(vin)
				.flatMap( lock -> Mono.just(Objects.nonNull(lock.getKeyLock()) ? "LOCKED" : "UNLOCKED") );
	}

	@Transactional
	public Mono<String> lockCar(String vin) {
		String keyLock = UUID.randomUUID().toString();
		return carLockRepository
				.lockCar(vin, keyLock)
				.filter(CarLockRepository::isSuccess)
				.flatMap(i -> Mono.just(keyLock));
	}
	
	@Transactional
	public Mono<String> unlockCar(String vin,String keyLock) {
		return carLockRepository
				.unlockCar(vin, keyLock)
				.filter(CarLockRepository::isSuccess)
				.flatMap( i-> Mono.just(i.toString()));
	}
}
