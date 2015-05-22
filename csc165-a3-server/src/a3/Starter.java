package a3;

import games.circuitshooter.CircuitShooterServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import sage.networking.IGameConnection.ProtocolType;

public class Starter {

    public static void main(String args[]) {
        String enableAIStr;
        boolean enableAI;
        int port;
        
        try {
            System.out.println("Current Address: " + InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        
        // Get port
        Scanner s = new Scanner(System.in);
        System.out.print("Enter desired port number: ");
        port = s.nextInt();
        
        do {
            System.out.print("Enable AI? (Y/N)\n(Can cause StreamCorruptedExceptions): ");
            enableAIStr = s.next();
        } while (!(enableAIStr.equalsIgnoreCase("y") || enableAIStr.equalsIgnoreCase("n")));

        s.close();
        
        enableAI = enableAIStr.equalsIgnoreCase("y") ? true : false; 
        
        try {
            new CircuitShooterServer(port, ProtocolType.TCP, enableAI);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
