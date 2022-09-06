package guru.sfg.beer.order.service.sm.actions;

import guru.sfg.beer.order.service.services.BeerOrderManagerImpl;
import guru.sfg.beer.order.service.config.Configuration.RabbitMqConfig;
import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.web.mappers.BeerOrderMapper;
import guru.springframework.common.model.events.AllocateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {


    private final BeerOrderRepository beerOrderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final BeerOrderMapper beerOrderMapper;

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        String beerOrderId = (String) stateContext.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER);
        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(UUID.fromString(beerOrderId));
        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            AllocateOrderRequest allocateOrderRequest = AllocateOrderRequest.builder()
                    .beerOrderDto(beerOrderMapper.beerOrderToDto(beerOrder))
                    .build();
            rabbitTemplate.convertAndSend(RabbitMqConfig.MICROSERVICES_CLASS_EXCHANGE_DIRECT_ALLOCATE_ORDER,RabbitMqConfig.MICROSERVICES_CLASS_DIRECT_ROUTING_KEY_ALLOCATE_ORDER,
                    allocateOrderRequest);
                log.debug("Sent allocation request to queue for Order ID {} and ORDER {} ", allocateOrderRequest.getBeerOrderDto().getId(), allocateOrderRequest);

        }, () -> log.error("Beer Order not found"));



    }
}
