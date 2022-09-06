package guru.sfg.beer.inventory.service.config.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String MICROSERVICES_CLASS_QUEUE_DIRECT = "microservices.class.queue.direct";
    public static final String MICROSERVICES_CLASS_EXCHANGE_DIRECT = "microservices.class.exchange.direct";
    public static final String MICROSERVICES_CLASS_DIRECT_ROUTING_KEY = "microservices.class.direct.routing.key";

    public static final String MICROSERVICES_CLASS_QUEUE_DIRECT_ALLOCATE_ORDER = "microservices.class.queue.direct.allocate";
    public static final String MICROSERVICES_CLASS_EXCHANGE_DIRECT_ALLOCATE_ORDER = "microservices.class.exchange.direct.allocate";
    public static final String MICROSERVICES_CLASS_DIRECT_ROUTING_KEY_ALLOCATE_ORDER = "microservices.class.direct.routing.key.allocate";

    public static final String MICROSERVICES_CLASS_QUEUE_DIRECT_ALLOCATION_FAILURE = "microservices.class.queue.direct.allocation_failure";
    public static final String MICROSERVICES_CLASS_EXCHANGE_DIRECT_ALLOCATION_FAILURE = "microservices.class.exchange.direct.allocation_failure";
    public static final String MICROSERVICES_CLASS_DIRECT_ROUTING_KEY_ALLOCATION_FAILURE = "microservices.class.direct.routing.key.allocation_failure";

    public static final String MICROSERVICES_CLASS_QUEUE_DIRECT_DEALLOCATE_ORDER = "microservices.class.queue.direct.deallocate";
    public static final String MICROSERVICES_CLASS_EXCHANGE_DIRECT_DEALLOCATE_ORDER = "microservices.class.exchange.direct.deallocate";
    public static final String MICROSERVICES_CLASS_DIRECT_ROUTING_KEY_DEALLOCATE_ORDER = "microservices.class.direct.routing.key.deallocate";

    @Bean
    Queue queue(){
        return new Queue(MICROSERVICES_CLASS_QUEUE_DIRECT, false);
    }

    @Bean
    DirectExchange directExchange(){
        return new DirectExchange(MICROSERVICES_CLASS_EXCHANGE_DIRECT);
    }

    @Bean
    Binding binding(@Qualifier("queue") Queue queue, @Qualifier("directExchange") DirectExchange directExchange){
        return BindingBuilder.bind(queue)
                .to(directExchange)
                .with(MICROSERVICES_CLASS_DIRECT_ROUTING_KEY);
    }

    @Bean
    Queue queueAllocate(){
        return new Queue(MICROSERVICES_CLASS_QUEUE_DIRECT_ALLOCATE_ORDER, false);
    }

    @Bean
    DirectExchange directExchangeAllocate(){
        return new DirectExchange(MICROSERVICES_CLASS_EXCHANGE_DIRECT_ALLOCATE_ORDER);
    }

    @Bean
    Binding bindingAllocate(@Qualifier("queueAllocate") Queue queueAllocate,@Qualifier("directExchangeAllocate") DirectExchange directExchangeAllocate){
        return BindingBuilder.bind(queueAllocate)
                .to(directExchangeAllocate)
                .with(MICROSERVICES_CLASS_DIRECT_ROUTING_KEY_ALLOCATE_ORDER);
    }

    @Bean
    Queue queueAllocateFailure(){
        return new Queue(MICROSERVICES_CLASS_QUEUE_DIRECT_ALLOCATION_FAILURE, false);
    }

    @Bean
    DirectExchange directExchangeAllocateFailure(){
        return new DirectExchange(MICROSERVICES_CLASS_EXCHANGE_DIRECT_ALLOCATION_FAILURE);
    }

    @Bean
    Binding bindingAllocateFailure(@Qualifier("queueAllocateFailure") Queue queueAllocateFailure, @Qualifier("directExchangeAllocateFailure") DirectExchange directExchangeAllocateFailure){
        return BindingBuilder.bind(queueAllocateFailure)
                .to(directExchangeAllocateFailure)
                .with(MICROSERVICES_CLASS_DIRECT_ROUTING_KEY_ALLOCATION_FAILURE);
    }

    @Bean
    Queue queueDeallocate(){
        return new Queue(MICROSERVICES_CLASS_QUEUE_DIRECT_DEALLOCATE_ORDER, false);
    }

    @Bean
    DirectExchange directExchangeDeallocate(){
        return new DirectExchange(MICROSERVICES_CLASS_EXCHANGE_DIRECT_DEALLOCATE_ORDER);
    }

    @Bean
    Binding bindingDeallocate(@Qualifier("queueDeallocate") Queue queueDeallocate, @Qualifier("directExchangeDeallocate") DirectExchange directExchangeDeallocate){
        return BindingBuilder.bind(queueDeallocate)
                .to(directExchangeDeallocate)
                .with(MICROSERVICES_CLASS_DIRECT_ROUTING_KEY_DEALLOCATE_ORDER);
    }

    @Bean
    MessageConverter messageConverter(ObjectMapper mapper){
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, ObjectMapper mapper){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter(mapper));
        return rabbitTemplate;
    }

}
