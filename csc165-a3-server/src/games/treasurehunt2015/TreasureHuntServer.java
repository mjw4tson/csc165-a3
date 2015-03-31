package games.treasurehunt2015;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import sage.networking.server.GameConnectionServer;
import sage.networking.server.IClientInfo;

public class TreasureHuntServer extends GameConnectionServer<UUID> {

    public TreasureHuntServer(int localPort, ProtocolType protocolType) throws IOException {
        super(localPort, protocolType);
    }
    
    @Override
    public void acceptClient(IClientInfo ci, Object o) {
        String message = (String)o;
        String[] msgTokens = message.split(",");
        
        if (msgTokens.length > 0) {
            if (msgTokens[0].compareTo("join") == 0) {
                UUID clientID = UUID.fromString(msgTokens[1]);
                addClient(ci, clientID);
                sendJoinedMsg(clientID, true);
            }
        }
    }
    
    public void processPacket(Object o, InetAddress senderIP, int sndPort) {
        String message = (String)o;
        String msgTokens[] = message.split(",");
        
        if (msgTokens.length > 0) {
            switch(msgTokens[0]) {
                case "bye":
                    break;
                case "create":
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
            String message = new String("join,");
            if (success)
                message += "success";
            else
                message += "failure";
            
            sendPacket(message, clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendCreateMsgs(UUID clientID, String[] position) {
        // Format create,remoteID,x,y,z
        try {
            String message = new String("create," + clientID.toString());
            message += "," + position[0];
            message += "," + position[1];
            message += "," + position[2];

            forwardPacketToAll(message, clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendDetailsMsg(UUID clientID, UUID remoteID, String[] position) {
        
    }
    
    public void sendMoveMsgs(UUID clientID, String[] position) {
        
    }
    
    public void sendWantsDetailsMsgs(UUID clientID) {
        
    }
}
