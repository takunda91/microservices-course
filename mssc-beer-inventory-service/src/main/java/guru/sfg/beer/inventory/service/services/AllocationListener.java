package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.config.Configuration.RabbitMqConfig;
import guru.springframework.common.model.events.AllocateOrderRequest;
import guru.springframework.common.model.events.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationListener {

    private final AllocationService allocationService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMqConfig.MICROSERVICES_CLASS_QUEUE_DIRECT_ALLOCATE_ORDER)
    public void listen(AllocateOrderRequest request){
        log.debug("Allocation order request receveied | {}", request);
        AllocateOrderResult.AllocateOrderResultBuilder builder = AllocateOrderResult.builder();
        builder.beerOrderDto(request.getBeerOrderDto());

        try{
            Boolean allocationResult = allocationService.allocateOrder(request.getBeerOrderDto());

            if (allocationResult){
                builder.pendingInventory(false);
            } else {
                builder.pendingInventory(true);
            }
            builder.allocationError(false);
        } catch (Exception e){
            log.error("Allocation failed for Order Id:" + request.getBeerOrderDto().getId());
            builder.allocationError(true);
        }

        rabbitTemplate.convertAndSend(RabbitMqConfig.MICROSERVICES_CLASS_EXCHANGE_DIRECT,RabbitMqConfig.MICROSERVICES_CLASS_DIRECT_ROUTING_KEY,
                builder.build());
        log.debug("Allocation result sent | {}", builder.build().getBeerOrderDto().getId());


    }


}
