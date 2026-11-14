package estacionamiento.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.boot.CommandLineRunner;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_DISPONIBILIDAD =
            "disponibilidad.exchange";

    public static final String COLA_ENTRADA_1 = "cola.entrada.1";
    public static final String COLA_ENTRADA_2 = "cola.entrada.2";
    public static final String COLA_ENTRADA_3 = "cola.entrada.3";

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public FanoutExchange disponibilidadExchange() {
        return new FanoutExchange(EXCHANGE_DISPONIBILIDAD);
    }

    @Bean
    public Queue colaEntrada1() {
        return new Queue(COLA_ENTRADA_1, true);
    }

    @Bean
    public Queue colaEntrada2() {
        return new Queue(COLA_ENTRADA_2, true);
    }

    @Bean
    public Queue colaEntrada3() {
        return new Queue(COLA_ENTRADA_3, true);
    }

    @Bean
    public Binding bindingEntrada1() {
        return BindingBuilder.bind(colaEntrada1())
                .to(disponibilidadExchange());
    }

    @Bean
    public Binding bindingEntrada2() {
        return BindingBuilder.bind(colaEntrada2())
                .to(disponibilidadExchange());
    }

    @Bean
    public Binding bindingEntrada3() {
        return BindingBuilder.bind(colaEntrada3())
                .to(disponibilidadExchange());
    }

    @Bean
    public CommandLineRunner comprobarRabbit(RabbitAdmin rabbitAdmin) {
        return args -> {
            System.out.println("========== RABBIT CONFIG CARGADO ==========");

            System.out.println("Declarando exchanges y colas...");

            rabbitAdmin.initialize();

            System.out.println("========== RABBIT INICIALIZADO ==========");
        };
    }

    @Bean
    public Jackson2JsonMessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
