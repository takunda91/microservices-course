package guru.springframework.msscbeerservice.services.order;

import guru.springframework.common.model.events.ValidateOrderRequest;
import guru.springframework.common.model.events.ValidateOrderResult;
import guru.springframework.msscbeerservice.config.Direct.Configuration.RabbitMqConfig;
import guru.springframework.msscbeerservice.config.Fanout.Configuration.FanoutRabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BeerOrderValidationListener {

    private final BeerOrderValidator beerOrderValidator;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = FanoutRabbitMqConfig.MICROSERVICES_CLASS_BEER_BREWING_QUEUE)
    public void listenToAnOrder(ValidateOrderRequest validateOrderRequest) {
        Boolean isValid = beerOrderValidator.validateOrder(validateOrderRequest.getBeerOrderDto());

        //happy here
        log.debug("Validate Status | " + isValid + " for order | {} " , validateOrderRequest.getBeerOrderDto().getId());
        rabbitTemplate.convertAndSend(RabbitMqConfig.MICROSERVICES_CLASS_EXCHANGE_DIRECT, RabbitMqConfig.MICROSERVICES_CLASS_DIRECT_ROUTING_KEY,
                ValidateOrderResult.builder()
                        .orderId(validateOrderRequest.getBeerOrderDto().getId())
                        .isValid(isValid)
                        .build());

    }

}
