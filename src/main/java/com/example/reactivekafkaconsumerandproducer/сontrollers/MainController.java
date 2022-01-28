package com.example.reactivekafkaconsumerandproducer.сontrollers;

import com.example.reactivekafkaconsumerandproducer.dto.FakeProducerDTO;
import com.example.reactivekafkaconsumerandproducer.service.ReactiveConsumerService;
import com.example.reactivekafkaconsumerandproducer.service.ReactiveProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/controller")
public class MainController {

    @Autowired
    private ReactiveConsumerService reactiveConsumerService;

    @Autowired
    private ReactiveProducerService reactiveProducerService;

    @GetMapping("/send")
    public boolean sendMessage(){
        reactiveProducerService.send(new FakeProducerDTO("4244"));
        return true;
    }
}
