package a3;

import games.circuitshooter.CircuitShooterServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import sage.networking.IGameConnection.ProtocolType;

public class Starter {

    public static void main(String args[]) {
        try {
            System.out.println("Current Address: " + InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        
        // Get port
        Scanner s = new Scanner(System.in);
        System.out.print("Enter desired port number: ");
        int port = s.nextInt();
        s.close();
        
        try {
            new CircuitShooterServer(port, ProtocolType.TCP);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
