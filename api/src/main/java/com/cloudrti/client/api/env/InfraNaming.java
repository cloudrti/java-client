package com.cloudrti.client.api.env;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by paulb on 12/05/16.
 */
public class InfraNaming {

    private String hostname;

    public String getPodName() {
        if(hostname != null) {
            return hostname;
        } else {
            try {
                hostname = InetAddress.getLocalHost().getHostName();
                return hostname;
            } catch (UnknownHostException e) {
                e.printStackTrace();

                return "Unknown";
            }
        }
    }

    public String getAppName() {
        String appName = getProperty("APP_NAME");
        if(appName == null || appName.length() == 0) {
            appName = "UnknownApp";
        }
        return appName;
    }

    public String getVersion() {
        String version = getProperty("APP_VERSION");
        if(version == null || version.length() == 0) {
            version = "UnknownVersion";
        }

        return version;
    }

    public String getNamespace() {
        String namespace = getProperty("POD_NAMESPACE");
        if(namespace == null || namespace.length() == 0) {
            namespace = "UnknownNamespace";
        }

        return namespace;
    }

    private String getProperty(String name) {
        String value = System.getProperty(name);
        if(value == null) {
            value = System.getenv(name);
        }

        return value;
    }
}
