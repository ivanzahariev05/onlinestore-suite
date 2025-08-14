package softuni.bg.supplementsonlinestore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.format.DateTimeFormatter;

@Configuration
public class Config {

    @Bean
    public PasswordEncoder passEncoder() {
        return new BCryptPasswordEncoder();
    }


}
