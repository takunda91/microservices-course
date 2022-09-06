package guru.springframework.msscbeerservice.services.brewery;

import guru.springframework.common.model.events.BrewBeerEvent;
import guru.springframework.msscbeerservice.config.Fanout.Configuration.FanoutRabbitMqConfig;
import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.services.inventory.BeerInventoryService;
import guru.springframework.msscbeerservice.services.repositories.BeerRepository;
import guru.springframework.msscbeerservice.web.mappers.BeerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrewingService {

    private final BeerRepository beerRepository;
    private final BeerInventoryService beerInventoryService;
    private final RabbitTemplate rabbitTemplate;
    private final BeerMapper beerMapper;


    @Scheduled(fixedRate = 5000)
    public void checkForLowInventory() {
        List<Beer> beers = beerRepository.findAll();
        beers.forEach(beer -> {
            Integer inventoryOnHand = beerInventoryService.getOnhandInventory(beer.getId());

            if(beer.getMinOnHand() >= inventoryOnHand){
                log.debug("Pushing Inventory request for | {}", beer);
                BrewBeerEvent brewBeerEvent = new BrewBeerEvent(beerMapper.beerToBeerDto(beer));
                log.debug("Brew Beer Event | {}", brewBeerEvent.getBeerDto().getId());

                rabbitTemplate.convertAndSend(FanoutRabbitMqConfig.MICROSERVICES_CLASS_EXCHANGE_FANOUT, "", brewBeerEvent );
               }

        } );

    }

}
