package de.dereingerostete.bungeebridge.util;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class IPAddress {
    private final String ipAddress;
    private final int port;

    public IPAddress(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    /**
     * Gets the ip address
     * <b>Note: This can also be a unix address</b>
     * @return The ip address
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Checks weather the ip address is a unix address
     * @return true if ip address is a unix address; false if otherwise
     */
    public boolean isUnixAddress() {
        return ipAddress.startsWith("unix://");
    }

    /**
     * Gets the port of the player's ip
     * @return The port ranging from 0 to 65.535
     */
    public int getPort() {
        return port;
    }

}
