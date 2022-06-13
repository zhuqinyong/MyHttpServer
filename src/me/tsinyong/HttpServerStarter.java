package me.tsinyong;

import java.util.Properties;

public class HttpServerStarter {
    private final static Properties PROPERTIES = new Properties();

    public void port() {
        PROPERTIES.put("port", 9090);
    }

    public static void start()
    {

    }
}
