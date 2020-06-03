package book.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration()
					.setFieldAccessLevel(AccessLevel.PRIVATE)
					.setFieldMatchingEnabled(true)
					.setDestinationNameTokenizer(NameTokenizers.UNDERSCORE)
					.setSourceNameTokenizer(NameTokenizers.UNDERSCORE);
		return modelMapper;
	}

}
