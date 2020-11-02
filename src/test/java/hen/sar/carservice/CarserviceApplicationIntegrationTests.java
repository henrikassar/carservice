package hen.sar.carservice;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import hen.sar.carservice.CarserviceApplication;
import hen.sar.carservice.dto.CarDto;
import hen.sar.carservice.dto.CarLockDto;
import hen.sar.carservice.entities.CarEntity;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CarserviceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
class CarserviceApplicationIntegrationTests {

	@Autowired private WebTestClient webTestClient;

	static private CarDto newCarRequest           = CarDto.builder().vin("1223-324324-32432").make("AUDI").model("A6").plateNumber(null).build();
	static private CarDto brokenCarRequest        = CarDto.builder().vin("8888-324324-32432").make(null).model("A6").plateNumber("LLL-000").build();
	static private CarDto nonExistentMakeRequest  = CarDto.builder().vin("0000-324324-32432").make("XYZ").model("100").plateNumber(null).build();
	static private CarDto nonExistentModelRequest = CarDto.builder().vin("0000-324324-32432").make("AUDI").model("M3").plateNumber(null).build();
	static private CarLockDto aquiredLock = null;

	@Test
	@Order(0)
	void contextLoads() {
	}
	
	@Test
	@Order(1)
	void listCars() {
		webTestClient
		.get().uri("/car")
		.exchange()
		.expectStatus().isOk()
		.expectHeader().valueEquals("Content-Type", MediaType.APPLICATION_STREAM_JSON.toString())
		.expectBodyList(CarEntity.class)
		.hasSize(0);	
	}

	@Test
	@Order(2)
	void checkLockForUnknownVin() {
		webTestClient
		.get().uri("/car/{vin}/lockstate","unknown-vin")
		.exchange()
		.expectStatus().isNotFound();
	}

	@Test
	@Order(3)
	void createCarTest() {
		
		webTestClient
		.post().uri("/car")
		.contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(newCarRequest), CarDto.class)
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().valueEquals("Content-Type", MediaType.APPLICATION_JSON.toString())
		.expectBody()
		.jsonPath("$.vin").isEqualTo(newCarRequest.getVin())    
		.jsonPath("$.make").isEqualTo(newCarRequest.getMake())    
		.jsonPath("$.model").isEqualTo(newCarRequest.getModel())    
		.jsonPath("$.plateNumber").isEqualTo(newCarRequest.getPlateNumber());    
		
	}

	@Test
	@Order(6)
	void getCarJustCreated() {
		webTestClient
		.get().uri("/car/{vin}",newCarRequest.getVin())
		.exchange()
		.expectStatus().isOk()
		.expectHeader().valueEquals("Content-Type", MediaType.APPLICATION_JSON.toString())
		.expectBody()
		.jsonPath("$.vin").isEqualTo(newCarRequest.getVin())    
		.jsonPath("$.make").isEqualTo(newCarRequest.getMake())    
		.jsonPath("$.model").isEqualTo(newCarRequest.getModel())    
		.jsonPath("$.plateNumber").isEqualTo(newCarRequest.getPlateNumber());    
	}

	@Test
	@Order(5)
	void listCarsAgain() {
		webTestClient
		.get().uri("/car")
		.exchange()
		.expectStatus().isOk()
		.expectHeader().valueEquals("Content-Type", MediaType.APPLICATION_STREAM_JSON.toString())
		.expectBodyList(CarDto.class)
		.hasSize(1)
		.contains(newCarRequest);	
	}

	@Test
	@Order(6)
	void checkLockForCarJustCreated() {
		webTestClient
		.get().uri("/car/{vin}/lockstate",newCarRequest.getVin())
		.exchange()
		.expectStatus().isOk()
		.expectHeader().valueEquals("Content-Type", MediaType.APPLICATION_JSON.toString())
		.expectBody()
		.jsonPath("$.state").isEqualTo("UNLOCKED");   
	}

	@Test
	@Order(7)
	void lockCarJustCreated() {
		aquiredLock= webTestClient
		.patch().uri("/car/{vin}/lock",newCarRequest.getVin())
		.exchange()
		.expectStatus().isOk()
		.expectHeader().valueEquals("Content-Type", MediaType.APPLICATION_JSON.toString())
		.expectBody(CarLockDto.class)
		.returnResult()
		.getResponseBody();
		
		assertTrue( aquiredLock.getVin().equals(newCarRequest.getVin()));
		assertTrue( !aquiredLock.getKeyLock().isEmpty() );
	}

	
	@Test
	@Order(8)
	void checkLockForCarJustLocked() {
		webTestClient
		.get().uri("/car/{vin}/lockstate",newCarRequest.getVin())
		.exchange()
		.expectStatus().isOk()
		.expectHeader().valueEquals("Content-Type", MediaType.APPLICATION_JSON.toString())
		.expectBody()
		.jsonPath("$.state").isEqualTo("LOCKED");   
	}

	@Test
	@Order(9)
	void lockCarWithoutUnlock() {
		webTestClient
		.patch().uri("/car/{vin}/lock",newCarRequest.getVin())
		.exchange()
		.expectStatus().isBadRequest();
	}


	@Test
	@Order(10)
	void unlockCarJustCreated() {
		webTestClient
		.patch().uri("/car/{vin}/unlock/{keyLock}",aquiredLock.getVin(),aquiredLock.getKeyLock())
		.exchange()
		.expectStatus().isOk()
		.expectHeader().valueEquals("Content-Type", MediaType.APPLICATION_JSON.toString())
		.expectBody()
		.jsonPath("$.vin").isEqualTo(newCarRequest.getVin())
		.jsonPath("$.keyLock").isEmpty();
	}

	@Test
	@Order(11)
	void unlockCarJustCreatedAdditionally() {
		webTestClient
		.patch().uri("/car/{vin}/unlock/{keyLock}",aquiredLock.getVin(),aquiredLock.getKeyLock())
		.exchange()
		.expectStatus().isBadRequest();
	}
	

	@Test
	@Order(12)
	void createCarFromBrokenDataTest() {
		
		webTestClient
		.post().uri("/car")
		.contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(brokenCarRequest), CarDto.class)
		.exchange()
		.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@Test
	@Order(13)
	void createCarFromNonExistentMakeTest() {
		
		webTestClient
		.post().uri("/car")
		.contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(nonExistentMakeRequest), CarDto.class)
		.exchange()
		.expectStatus().isNotFound();
	}
	@Test
	@Order(14)
	void createCarFromnonExistentModelTest() {
		
		webTestClient
		.post().uri("/car")
		.contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(nonExistentModelRequest), CarDto.class)
		.exchange()
		.expectStatus().isNotFound();
	}
	
}
