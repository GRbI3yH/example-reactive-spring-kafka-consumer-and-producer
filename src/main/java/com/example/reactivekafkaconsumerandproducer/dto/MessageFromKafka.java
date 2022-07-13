package com.example.reactivekafkaconsumerandproducer.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("MessageEvent")
public class MessageFromKafka {

    @JsonProperty("type")
    private String type;

    @JsonProperty("data")
    private String data;

    @JsonProperty("date")
    private String date;

    @JsonCreator
    public MessageFromKafka(@JsonProperty("type") String type,
                            @JsonProperty("data") String data,
                            @JsonProperty("date") String date) {
        this.type = type;
        this.data = data;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
