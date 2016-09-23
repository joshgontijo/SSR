package com.josue.micro.registry.client.discovery;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * Created by Josue on 26/08/2016.
 */
public class LocalDiscovery implements Discovery {

    public static void main(String[] args){
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n");
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.out.println(new LocalDiscovery().resolveHost());
    }

    private static final Logger logger = Logger.getLogger(LocalDiscovery.class.getName());


    @Override
    public String resolveHost() {
        InetAddress candidateAddress = null;
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                Enumeration<InetAddress> inetAddresses = nic.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    String address = inetAddress.getHostAddress();
                    String nicName = nic.getName();

                    logger.info("NIC: " + nicName + " -> Address: " + inetAddress + " -> IPV4: " + (inetAddress instanceof Inet4Address));

                    if (nicName.startsWith("eth0") || nicName.startsWith("en0")) {
                        return address;
                    }
                    //use IPV4 only
                    if ((nicName.endsWith("0") || candidateAddress == null) && inetAddress instanceof Inet4Address) {
                        candidateAddress = inetAddress;
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException("Cannot resolve local network address", e);
        }
        return candidateAddress == null ? "127.0.0.1" : candidateAddress.getHostAddress();
    }


}
