package hen.sar.carservice.handler;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.net.URI;

import javax.validation.Validator;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;

import hen.sar.carservice.dto.CarDto;
import hen.sar.carservice.dto.CarLockDto;
import hen.sar.carservice.dto.CarLockStateDto;
import hen.sar.carservice.mappers.CarServiceMappers;
import hen.sar.carservice.proxy.CarserviceProxy;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CarserviceHandler {

	private static final String PATH_KEYLOCK = "keylock";
	private static final String PATH_VIN = "vin";
	private final CarserviceProxy proxy; 
	private final Validator carDtoValidator;
	
	public Mono<ServerResponse> listCars(ServerRequest request) {
		return ok()
				.contentType(APPLICATION_STREAM_JSON)
				.body(proxy.findAll().map(CarServiceMappers::toCarDto), CarDto.class)
				.log();
	}

	public Mono<ServerResponse> createCar(ServerRequest request) {
		return
			request
			.bodyToMono(CarDto.class)
			.doOnNext(this::validateDto)
			.map( CarServiceMappers::toCarEntity )
			.flatMap( ce -> proxy.validateOnPublicApi(ce).flatMap( cm -> proxy.createCar(ce) ))
			.map( CarServiceMappers::toCarDto )
			.flatMap( car -> created(URI.create("/car/" + car.getVin())).body(fromValue(car)))
			.onErrorResume(ServerWebInputException.class, e -> ServerResponse.unprocessableEntity().build())
			.switchIfEmpty(notFound().build())
			.log();
			
}
	
	public Mono<ServerResponse> carLockState(ServerRequest request) {
		return proxy
				.getCarCurrentLockState(request.pathVariable(PATH_VIN))
				.map( CarLockStateDto::new )
				.flatMap(state -> okReply().body( fromValue(state)))
				.switchIfEmpty(notFound().build())
				.log();
	}

	public Mono<ServerResponse> lockCar(ServerRequest request) {
		return proxy
				.lockCar(request.pathVariable(PATH_VIN))
				.map( key -> new CarLockDto(request.pathVariable(PATH_VIN),key) )
				.flatMap( rc -> okReply().body(fromValue(rc)))
				.switchIfEmpty(badRequest().build())
				.log();
	}

	public Mono<ServerResponse> unlockCar(ServerRequest request) {
		return proxy
				.unlockCar(request.pathVariable(PATH_VIN),request.pathVariable(PATH_KEYLOCK))
				.map( key -> new CarLockDto(request.pathVariable(PATH_VIN),null) )
				.flatMap( rc -> okReply().body(fromValue(rc)))
				.switchIfEmpty(badRequest().build())
				.log();
	}

	public Mono<ServerResponse> find(ServerRequest request) {
	return proxy
			.findCarByVin(request.pathVariable(PATH_VIN))
			.map(CarServiceMappers::toCarDto)
			.flatMap(car -> okReply().body(fromValue(car)))
			.switchIfEmpty(notFound().build());
	}
	
	private static ServerResponse.BodyBuilder okReply() 
	{
		return ok().contentType(APPLICATION_JSON);
	}

	private void validateDto(CarDto cardto) {
		if ( !carDtoValidator.validate(cardto).isEmpty() )
			throw new ServerWebInputException("Request contains invalid data");
	}
	
}