package guru.sfg.beer.order.service.config.Configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FanoutRabbitMqConfig {

    public static final String MICROSERVICES_CLASS_BEER_BREWING_QUEUE = "microservices.class.beer.brewing.queue";
    public static final String MICROSERVICES_CLASS_EXCHANGE_FANOUT = "microservices.class.exchange.fanout";

    @Bean
    Queue fanoutBeerQueue(){
        return new Queue(MICROSERVICES_CLASS_BEER_BREWING_QUEUE, false);
    }


    @Bean
    FanoutExchange fanoutBeerExchange(){
        return new FanoutExchange(MICROSERVICES_CLASS_EXCHANGE_FANOUT);
    }


    @Bean
    Binding fanoutBeerBinding(Queue fanoutBeerQueue, FanoutExchange fanoutBeerExchange){
        return BindingBuilder.bind(fanoutBeerQueue)
                .to(fanoutBeerExchange);
    }

    @Bean
    MessageConverter fanoutOutMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate fanoutRabbitMq(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(fanoutOutMessageConverter());
        return rabbitTemplate;
    }

}
