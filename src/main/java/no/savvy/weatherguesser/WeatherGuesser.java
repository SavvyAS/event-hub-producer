package no.savvy.weatherguesser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
public class WeatherGuesser {

    private List<String> weathers = List.of("sun", "rain", "snow", "thunder", "sleet", "storms");
    private static final Logger log = LoggerFactory.getLogger(WeatherGuesser.class);
    @Autowired
    private Sinks.Many<Message<String>> many;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        Random rand = new Random();
        var date = dateFormat.format(new Date());
        var weather = weathers.get(rand.nextInt(weathers.size()));
        var pod = System.getenv("POD__NAME");
        pod = (pod != null) ? pod : "POD__NAME";
        var message = String.format("It's now %s, and I think tomorrow it will be %s. Sincerely, %s", date, weather, pod);
        many.emitNext(MessageBuilder.withPayload(message).build(), Sinks.EmitFailureHandler.FAIL_FAST);
        log.info("Sent: {}", message);
    }
}
