package guru.sfg.beer.order.service.services.listeners;

import guru.sfg.beer.order.service.services.BeerOrderManager;
import guru.sfg.beer.order.service.config.Configuration.RabbitMqConfig;
import guru.springframework.common.model.events.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderAllocationResultListener {

    private final BeerOrderManager beerOrderManager;

    @RabbitListener(queues = RabbitMqConfig.MICROSERVICES_CLASS_QUEUE_DIRECT)
    public void listen(AllocateOrderResult result) {
        if (result.getBeerOrderDto() != null) {
            log.debug("receive allocation order result | {}", result.getBeerOrderDto().getId());
            if (!result.getAllocationError() && !result.getPendingInventory()) {
                beerOrderManager.beerOrderAllocationPassed(result.getBeerOrderDto());
            } else if (!result.getAllocationError() && result.getPendingInventory()) {
                //pending inventory
                beerOrderManager.beerOrderAllocationPendingInventory(result.getBeerOrderDto());
            } else if (result.getAllocationError()) {
                //allocation error
                beerOrderManager.beerOrderAllocationFailed(result.getBeerOrderDto());
            }
        }
    }

}
