package com.example.reactivekafkaconsumerandproducer.сontrollers;

import com.example.reactivekafkaconsumerandproducer.dto.MessageFromKafka;
import com.example.reactivekafkaconsumerandproducer.service.ReactiveConsumerService;
import com.example.reactivekafkaconsumerandproducer.service.ReactiveProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/controller")
public class MainController {

    @Autowired
    private ReactiveConsumerService reactiveConsumerService;

    @Autowired
    private ReactiveProducerService reactiveProducerService;

    @GetMapping("/send")
    public boolean sendMessage(
            @RequestParam(defaultValue = "transactions") String messageType,
            @RequestParam(defaultValue = "659654f0-d5e4-4875-8e3e-2ecf1babd03b") String token
    ){
        reactiveProducerService.send(new MessageFromKafka(messageType,"{\n" +
                "      \"digital_wallet_token\": {\n" +
                "        \"token\": \"b98cb680-2fd4-4c14-aa56-8d05091209d5\",\n" +
                "        \"type\": \"fulfillment.requested\"\n" +
                "      },\n" +
                "      \"token\": \""+token+"\",\n" +
                "      \"type\": \"fulfillment.requested\",\n" +
                "      \"channel\": \"TOKEN_SERVICE_PROVIDER\",\n" +
                "      \"state\": \"REQUESTED\",\n" +
                "      \"fulfillment_status\": \"DECISION_YELLOW\",\n" +
                "      \"reason\": \"Additional identity verification required\",\n" +
                "      \"created_time\": \"2021-02-23T18:44:21Z\"\n" +
                "    }","2011-12-03T10:15:30"));
        return true;
    }
}
