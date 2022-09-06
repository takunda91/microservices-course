package guru.sfg.beer.inventory.service.config.Configuration;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("local-discovery")
@Component
@EnableDiscoveryClient
public class EurekaConfig {
}
