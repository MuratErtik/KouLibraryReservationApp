package org.koulibrary.koulibraryreservationapp.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("spring.flyway")
public class FlywayProperties {

    //  flyway:     baseline-on-migrate : true     validate-on-migrate : true     enabled: true
    boolean isEnabled;
}
