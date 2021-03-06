package games.circuitshooter;

import event.NPC;
import graphicslib3D.Point3D;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.UUID;

import sage.networking.server.GameConnectionServer;
import sage.networking.server.IClientInfo;

public class CircuitShooterServer extends GameConnectionServer<UUID> {
    private NPCController npcCtrl;
    private HashMap<UUID, Point3D> playerLocations;
    
    public CircuitShooterServer(int localPort, ProtocolType protocolType, boolean enableAI) throws IOException {
        super(localPort, protocolType);
        System.out.println("Listening for clients on port: " + localPort);
        
        playerLocations = new HashMap<UUID, Point3D>();
        
        npcCtrl = new NPCController(this);
        
        if (enableAI) {
            System.out.println("Starting NPC Loop");
            npcCtrl.npcLoop();
        }
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
                sendNPCInfo();
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
                    sendMoveMsgs(UUID.fromString(msgTokens[1]), 
                                 Float.parseFloat(msgTokens[2]), 
                                 Float.parseFloat(msgTokens[3]), 
                                 Float.parseFloat(msgTokens[4]));
                    break;
                    
                case "rot":
                    sendRotMsgs(UUID.fromString(msgTokens[1]),
                            Float.parseFloat(msgTokens[2]), 
                            Float.parseFloat(msgTokens[3]), 
                            Float.parseFloat(msgTokens[4]),
                            Float.parseFloat(msgTokens[5]));
                    break;
                    
                case "proj":
                    sendProjMsgs(UUID.fromString(msgTokens[1]));
                    break;
                    
                case "hit":
                    sendHitMsg(UUID.fromString(msgTokens[1]), UUID.fromString(msgTokens[2]), Boolean.parseBoolean(msgTokens[3]));
                    break;
                    
                case "dsfr":
                    sendDetailsMsg(UUID.fromString(msgTokens[1]),
                                   UUID.fromString(msgTokens[2]),
                                   Float.parseFloat(msgTokens[3]), 
                                   Float.parseFloat(msgTokens[4]), 
                                   Float.parseFloat(msgTokens[5]));
                    break;
                    
                case "wsds":
                    sendWantsDetailsMsgs(UUID.fromString(msgTokens[1]));
                    break;
            }
        }

        Thread.yield();
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
        // Format create,newID,x,y,z
        try {
            String msg = new String("create," + clientID.toString());
            msg += "," + x;
            msg += "," + y;
            msg += "," + z;

            playerLocations.put(clientID, new Point3D(x, y, z));
            System.out.println("Forwarding create: " + msg);
            forwardPacketToAll(msg, clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendDetailsMsg(UUID clientID, UUID remoteID, float x, float y, float z) {
        // Format dsfr,detailsID,requesterID,x,y,z
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
        // Format move,moveID,x,y,z
        try {
            String msg = new String("move," + clientID.toString());
            msg += "," + x;
            msg += "," + y;
            msg += "," + z;

            Point3D playerLoc = playerLocations.get(clientID);
            
            if (playerLoc != null) {
                playerLoc.setX(x);
                playerLoc.setY(x);
                playerLoc.setZ(x);
            }
            
            // System.out.println("Forwarding move: " + msg); Commented due to verbosity
            // Only send messages every 50ms to reduce network traffic
            forwardPacketToAll(msg, clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }
    
    public void sendRotMsgs(UUID clientID, float col0x, float col0z, float col2x, float col2z) {
        // Format rot,clientID,rotamt
        try {
            String msg = new String("rot," + clientID.toString());
            msg += "," + col0x + "," + col0z;
            msg += "," + col2x + "," + col2z;
            
            // System.out.println("Sending rotation message: " + msg); Commented due to verbosity
            forwardPacketToAll(msg, clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendProjMsgs(UUID clientID) {
        // Format proj,clientID
        try {
            String msg = new String("proj," + clientID.toString());
            
            forwardPacketToAll(msg, clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendHitMsg(UUID shooterID, UUID woundedID, boolean isKilled) {
        // Format hit,shooterID,woundedID,isKilled
        try {
            String msg = new String("hit," + shooterID.toString());
            msg += "," + woundedID;
            msg += "," + isKilled;
            
            sendPacket(msg, shooterID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendWantsDetailsMsgs(UUID clientID) {
        // Format wsds,requesterID
        try {
            String msg = new String("wsds," + clientID.toString());

            System.out.println("Forwarding wants details: " + msg);
            forwardPacketToAll(msg, clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }    
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
    
    public void sendNPCInfo() {
        NPC npc = npcCtrl.getNPC();

        // Format mnpc,uuid,x,y,z
        try {
            String msg = new String("mnpc," + npc.getId());
            msg += "," + (npc.getX());
            msg += "," + (npc.getY());
            msg += "," + (npc.getZ());

            // System.out.println("Updating NPC location: " + msg); // Commented due to verbosity
            sendPacketToAll(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public HashMap<UUID, Point3D> getPlayerLocations() {
        return playerLocations;
    }
}
