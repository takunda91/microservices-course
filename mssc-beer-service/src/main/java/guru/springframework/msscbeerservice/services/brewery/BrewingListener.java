package guru.springframework.msscbeerservice.services.brewery;


import guru.springframework.common.model.BeerDto;
import guru.springframework.common.model.events.BrewBeerEvent;
import guru.springframework.common.model.events.NewInventoryEvent;
import guru.springframework.msscbeerservice.config.Direct.Configuration.RabbitMqConfig;
import guru.springframework.msscbeerservice.config.Fanout.Configuration.FanoutRabbitMqConfig;
import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.services.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrewingListener {

    private final BeerRepository beerRepository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = FanoutRabbitMqConfig.MICROSERVICES_CLASS_BEER_BREWING_QUEUE)
    @Transactional
    public void listen(BrewBeerEvent brewBeerEvent){
        //todo check why this listener is returning now
        if(brewBeerEvent.getBeerDto() != null){
            log.info("Beer to send : {}", brewBeerEvent);
            BeerDto beerDto = brewBeerEvent.getBeerDto();
            Beer beer = beerRepository.getOne(beerDto.getId());
            beerDto.setQuantityOnHand(beer.getQuantityToBrew());
            NewInventoryEvent newInventoryEvent = new NewInventoryEvent(beerDto);
            rabbitTemplate.convertAndSend(RabbitMqConfig.MICROSERVICES_CLASS_EXCHANGE_DIRECT, RabbitMqConfig.MICROSERVICES_CLASS_DIRECT_ROUTING_KEY, newInventoryEvent);

        }
    }
}
