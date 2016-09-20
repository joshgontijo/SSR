package com.josue.micro.registry.client.discovery;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Josue on 26/08/2016.
 */
public class EC2Discovery implements Discovery {

    private static final String AWS_META_URL = "http://169.254.169.254/latest/meta-data/instance-id";

    @Override
    public String resolveHost() {
        try {
            URL url = new URL(AWS_META_URL);
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            return reader.readLine();
        } catch (Exception e) {
            throw new RuntimeException("Could not resolce EC2 host address", e);
        }
    }
}
