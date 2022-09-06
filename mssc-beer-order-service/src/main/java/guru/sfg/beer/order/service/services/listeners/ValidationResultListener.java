package guru.sfg.beer.order.service.services.listeners;

import guru.sfg.beer.order.service.services.BeerOrderManager;
import guru.sfg.beer.order.service.config.Configuration.RabbitMqConfig;
import guru.springframework.common.model.events.ValidateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ValidationResultListener {

    private final BeerOrderManager beerOrderManager;

    @RabbitListener(queues = RabbitMqConfig.MICROSERVICES_CLASS_QUEUE_DIRECT)
    public void Listen(ValidateOrderResult validateOrderResult){
        final UUID beerOrderId = validateOrderResult.getOrderId();

        log.debug("Validation result for order id | {}, -> {}", beerOrderId, validateOrderResult);

        beerOrderManager.processValidationResult(beerOrderId, validateOrderResult.getIsValid());

    }

}
