package games.treasurehunt2015;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import sage.networking.server.GameConnectionServer;
import sage.networking.server.IClientInfo;

public class TreasureHuntServer extends GameConnectionServer<UUID> {

    public TreasureHuntServer(int localPort, ProtocolType protocolType) throws IOException {
        super(localPort, protocolType);
        
        System.out.println("Listening for clients on port: " + localPort);
    }
    
    @Override
    public void acceptClient(IClientInfo ci, Object o) {
        String msg = (String)o;
        String[] msgTokens = msg.split(",");
        
        System.out.println("Accepting client with msg: " + msg);
        if (msgTokens.length > 0) {
            if (msgTokens[0].compareTo("join") == 0) {
                UUID clientID = UUID.fromString(msgTokens[1]);
                System.out.println("Adding client: " + clientID);
                addClient(ci, clientID);
                sendJoinedMsg(clientID, true);
            }
        }
    }
    
    public void processPacket(Object o, InetAddress senderIP, int sndPort) {
        String msg = (String)o;
        String msgTokens[] = msg.split(",");
        
        if (msgTokens.length > 0) {
            switch(msgTokens[0]) {
                case "bye":
                    sendByeMsg(UUID.fromString(msgTokens[1]));
                    break;
                    
                case "create":
                    sendCreateMsgs(UUID.fromString(msgTokens[1]), 
                                   Float.parseFloat(msgTokens[2]), 
                                   Float.parseFloat(msgTokens[3]), 
                                   Float.parseFloat(msgTokens[4]));
                    break;
                    
                case "move":
                    break;
                    
                case "dsfr":
                    
                    break;
            }
        }
    }
    
    public void sendJoinedMsg(UUID clientID, boolean success) {
        // Format join,success or join,failure
        try {
            String msg = new String("join,");
            if (success)
                msg += "success";
            else
                msg += "failure";
            
            System.out.println("Sending join: " + msg);
            sendPacket(msg, clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendCreateMsgs(UUID clientID, float x, float y, float z) {
        // Format create,remoteID,x,y,z
        try {
            String msg = new String("create," + clientID.toString());
            msg += "," + x;
            msg += "," + y;
            msg += "," + z;

            System.out.println("Forwarding create: " + msg);
            forwardPacketToAll(msg, clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendDetailsMsg(UUID clientID, UUID remoteID, float x, float y, float z) {
        // Format dsfr,remoteID,x,y,z
        try {
            String msg = new String("dsfr," + clientID);
            msg += "," + x;
            msg += "," + y;
            msg += "," + z;
            
            System.out.println("Sending dsfr: " + msg);
            sendPacket(msg, remoteID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendMoveMsgs(UUID clientID, float x, float y, float z) {
        // Format move,clientID,x,y,z
        try {
            String msg = new String("move," + clientID.toString());
            msg += "," + x;
            msg += "," + y;
            msg += "," + z;

            System.out.println("Forwarding move: " + msg);
            forwardPacketToAll(msg, clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }
    
    public void sendWantsDetailsMsgs(UUID clientID) {
        
    }
    
    public void sendByeMsg(UUID clientID) {
        // Format bye,clientID
        try {
            String msg = new String("bye," + clientID.toString());
            forwardPacketToAll(msg, clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Removing client: " + clientID);
        removeClient(clientID);
    }
}
