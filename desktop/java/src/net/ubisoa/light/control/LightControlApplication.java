package net.ubisoa.light.control;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class LightControlApplication{

    /**
     * @param args
     *            the command line arguments
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        /* Activate these lines to see log messages of JmDNS */
        JmDNS jmdns = JmDNS.create();
        try {
                ServiceInfo[] infos = jmdns.list("_openthings._tcp.local.");
                System.out.println("List _openthings._tcp.local.");
                for (int i = 0; i < infos.length; i++) {
                	InetAddress[] address = infos[i].getInetAddresses();
                    System.out.println(infos[i].getName());
                    System.out.println(infos[i].getServer());
                    System.out.println(infos[i].getPort());
                    System.out.println(infos[i].getNiceTextString());
                    for (int j = 0; j < address.length; j++) {
                    	System.out.println(address[j]);
                    }
                }
                System.out.println();
        } finally {
            if (jmdns != null) try {
                jmdns.close();
            } catch (IOException exception) {
                //
            }
        }
    }
}