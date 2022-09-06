package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.config.Configuration.RabbitMqConfig;
import guru.springframework.common.model.events.DeallocateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DeallocationListener {

    private final AllocationService allocationService;

   @RabbitListener(queues = RabbitMqConfig.MICROSERVICES_CLASS_QUEUE_DIRECT_DEALLOCATE_ORDER)
    public void listen(DeallocateOrderRequest request){
        allocationService.deallocateOrder(request.getBeerOrderDto());
    }

}
