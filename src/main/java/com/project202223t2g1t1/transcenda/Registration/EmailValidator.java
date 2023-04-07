package com.project202223t2g1t1.transcenda.Registration;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class EmailValidator implements Predicate<String> {

    @Override
    public boolean test(String s) {

        if (s.contains("@")) {
            return true;
        } else if (s.contains(".com")) {
            return true;
        } else {
            return false;
        }

    }
}





