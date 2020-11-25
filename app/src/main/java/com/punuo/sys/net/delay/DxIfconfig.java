package com.punuo.sys.net.delay;

public class DxIfconfig {
    public String inetAddr; // ip
    public String bcast;    // gateway
    public String mask;     // netmask

    public boolean isBlank() {
        return (inetAddr == null && bcast == null && mask == null);
    }

    public boolean isNotBlank() {
        return !isBlank();
    }
}
