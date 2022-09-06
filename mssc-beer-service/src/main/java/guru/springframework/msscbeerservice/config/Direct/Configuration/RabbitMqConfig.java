package guru.springframework.msscbeerservice.config.Direct.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String MICROSERVICES_CLASS_QUEUE_DIRECT = "microservices.class.queue.direct";
    public static final String MICROSERVICES_CLASS_EXCHANGE_DIRECT = "microservices.class.exchange.direct";
    public static final String MICROSERVICES_CLASS_DIRECT_ROUTING_KEY = "microservices.class.direct.routing.key";

    @Bean
    Queue queue(){
        return new Queue(MICROSERVICES_CLASS_QUEUE_DIRECT, false);
    }

    @Bean
    DirectExchange directExchange(){
        return new DirectExchange(MICROSERVICES_CLASS_EXCHANGE_DIRECT);
    }

    @Bean
    Binding binding(Queue queue, DirectExchange directExchange){
        return BindingBuilder.bind(queue)
                .to(directExchange)
                .with(MICROSERVICES_CLASS_DIRECT_ROUTING_KEY);
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
