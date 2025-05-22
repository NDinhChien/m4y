package com.ndinhchien.m4y.global.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentService {

    @Autowired
    private Environment environment;

    public boolean isProdEnv() {
        String[] profiles = environment.getActiveProfiles();
        if (profiles != null && profiles.length >= 1) {
            return profiles[0] == "prod";
        }
        return false;
    }
}
