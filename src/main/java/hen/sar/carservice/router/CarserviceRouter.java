package hen.sar.carservice.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import hen.sar.carservice.handler.CarserviceHandler;

@Configuration
public class CarserviceRouter {

  @Bean
  public RouterFunction<ServerResponse> buildRouter(CarserviceHandler carserviceHandler) {
    return RouterFunctions
    		.route()
    		.GET("/car", carserviceHandler::listCars)
    		.GET("/car/{vin}", carserviceHandler::find)
    		.GET("/car/{vin}/lockstate", carserviceHandler::carLockState)
    		.PATCH("/car/{vin}/lock", carserviceHandler::lockCar)
    		.PATCH("/car/{vin}/unlock/{keylock}", carserviceHandler::unlockCar)
    		.POST("/car", carserviceHandler::createCar)
    		.build();
  }
    
}