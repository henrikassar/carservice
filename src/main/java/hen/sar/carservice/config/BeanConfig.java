package hen.sar.carservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class BeanConfig {

	@Bean
	public Validator springValidator() {
		return new LocalValidatorFactoryBean();
	}

}
