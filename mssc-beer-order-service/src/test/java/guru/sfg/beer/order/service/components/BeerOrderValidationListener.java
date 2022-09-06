package guru.sfg.beer.order.service.components;

import guru.sfg.beer.order.service.config.Configuration.FanoutRabbitMqConfig;
import guru.sfg.beer.order.service.config.Configuration.RabbitMqConfig;
import guru.springframework.common.model.events.ValidateOrderRequest;
import guru.springframework.common.model.events.ValidateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BeerOrderValidationListener {

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = FanoutRabbitMqConfig.MICROSERVICES_CLASS_BEER_BREWING_QUEUE)
    public void listenToAnOrder(Message message) {

        boolean isValid = true;
        boolean sendResponse = true;

        ValidateOrderRequest validateOrderRequest = (ValidateOrderRequest) message.getPayload();
        if (validateOrderRequest.getBeerOrderDto().getCustomerRef() != null) {
            if (validateOrderRequest.getBeerOrderDto().getCustomerRef().equals("fail-validation")) {
                isValid = false;
            } else if (validateOrderRequest.getBeerOrderDto().getCustomerRef().equals("dont-validate")) {
                sendResponse = false;
            }
        }

        if (sendResponse) {
            rabbitTemplate.convertAndSend(RabbitMqConfig.MICROSERVICES_CLASS_EXCHANGE_DIRECT, RabbitMqConfig.MICROSERVICES_CLASS_DIRECT_ROUTING_KEY,
                    ValidateOrderResult.builder()
                            .orderId(validateOrderRequest.getBeerOrderDto().getId())
                            .isValid(isValid)
                            .build());
        }

    }

}
