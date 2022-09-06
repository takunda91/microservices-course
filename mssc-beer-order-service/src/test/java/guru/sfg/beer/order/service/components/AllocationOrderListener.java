package guru.sfg.beer.order.service.components;

import guru.sfg.beer.order.service.config.Configuration.RabbitMqConfig;
import guru.springframework.common.model.events.AllocateOrderRequest;
import guru.springframework.common.model.events.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllocationOrderListener {

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMqConfig.MICROSERVICES_CLASS_QUEUE_DIRECT_ALLOCATE_ORDER)
    public void listen(Message message) {
        boolean allocationError = false;
        boolean sendResponse = true;

        AllocateOrderRequest allocateOrderRequest = (AllocateOrderRequest) message.getPayload();
        log.debug("ALLOCATE ORDER REQ | {}",allocateOrderRequest);
        if (allocateOrderRequest.getBeerOrderDto().getCustomerRef() != null) {
            if (allocateOrderRequest.getBeerOrderDto().getCustomerRef().equals("fail-allocation")) {
                allocationError = true;
            } else if (allocateOrderRequest.getBeerOrderDto().getCustomerRef().equals("dont-allocate")) {
                sendResponse = false;
            }
        }

        allocateOrderRequest.getBeerOrderDto().getBeerOrderLines().forEach(beerOrderLineDto -> {
            beerOrderLineDto.setQuantityAllocated(beerOrderLineDto.getOrderQuantity());
        });

        AllocateOrderResult allocateOrderResult = AllocateOrderResult.builder()
                .beerOrderDto(allocateOrderRequest.getBeerOrderDto())
                .allocationError(allocationError)
                .pendingInventory(false).build();

        if (sendResponse) {
            rabbitTemplate.convertAndSend(RabbitMqConfig.MICROSERVICES_CLASS_EXCHANGE_DIRECT, RabbitMqConfig.MICROSERVICES_CLASS_DIRECT_ROUTING_KEY,
                    allocateOrderResult);
        }

    }

}
