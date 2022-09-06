package com.taku.springrabbitmq;

import com.taku.springrabbitmq.Direct.Configuration.RabbitMqConfig;
import com.taku.springrabbitmq.Direct.Model.Message;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class SpringRabbitMqApplication implements CommandLineRunner {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DirectExchange directExchange;

    @Autowired
    private FanoutExchange fanoutExchange;

    public static void main(String[] args) {
        SpringApplication.run(SpringRabbitMqApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        runDirectQueue();
        runFanoutQueue();

    }

    private void runDirectQueue(){
        System.out.println("Sending Direct Message");
        Message message = Message.builder()
                .message("Sending Direct Message to Queue")
                .shard(UUID.randomUUID())
                .build();
        rabbitTemplate.convertAndSend(directExchange.getName(), RabbitMqConfig.MICROSERVICES_CLASS_DIRECT_ROUTING_KEY,message);
        System.out.println("Message sent to Direct Queue");
    }

    private void runFanoutQueue(){
        System.out.println("Sending Fanout Message");
        Message message = Message.builder()
                .message("Sending Fanout Message to Queue")
                .shard(UUID.randomUUID())
                .build();
        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "",message);
        System.out.println("Message sent to Fanout Queue");
    }
}
