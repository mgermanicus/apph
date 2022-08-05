package com.viseo.apph.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FrontServer {
    @Value("${front-server}")
    String frontServer;

    public String getFrontServer(){
        return frontServer;
    }

}
