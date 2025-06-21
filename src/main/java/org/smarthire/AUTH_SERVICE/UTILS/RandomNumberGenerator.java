package org.smarthire.AUTH_SERVICE.UTILS;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomNumberGenerator {

    public Long generateOtp(){

        return ThreadLocalRandom.current().nextLong(100000,1000000);
    }
}
