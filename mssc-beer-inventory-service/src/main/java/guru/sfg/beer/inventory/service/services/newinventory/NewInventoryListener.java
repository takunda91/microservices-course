package guru.sfg.beer.inventory.service.services.newinventory;

import guru.sfg.beer.inventory.service.config.Configuration.RabbitMqConfig;
import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import guru.springframework.common.model.BeerDto;
import guru.springframework.common.model.events.NewInventoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewInventoryListener {

    private final BeerInventoryRepository beerInventoryRepository;

    @RabbitListener(queues = RabbitMqConfig.MICROSERVICES_CLASS_QUEUE_DIRECT)
   @Transactional
    public void listenInventory(NewInventoryEvent newInventoryEvent){
        //todo check why object is null
        if(newInventoryEvent.getBeerDto() != null){
            log.info("Received | {}", newInventoryEvent);

            BeerDto beerDto = newInventoryEvent.getBeerDto();
//        if(beerInventoryRepository.findById())
            BeerInventory beerInventory = BeerInventory.builder()
                    .beerId(beerDto.getId())
                    .upc(beerDto.getUpc())
                    .quantityOnHand(beerDto.getQuantityOnHand())
                    .build();
            beerInventoryRepository.save(beerInventory);
        }

    }

}
