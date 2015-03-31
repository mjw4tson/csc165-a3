package games.treasurehunt2015;

import graphicslib3D.Vector3D;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.Vector;

import sage.networking.client.GameConnectionClient;
import sage.scene.shape.Sphere;

public class TreasureHuntClient extends GameConnectionClient {
    private TreasureHunt game;
    private UUID id;
    private Vector<Sphere> ghostAvatars;
    
    public TreasureHuntClient(InetAddress remAddr, int remPort, ProtocolType pType, TreasureHunt game) throws IOException {
        super(remAddr, remPort, pType);
        
        this.game = game;
        this.id = UUID.randomUUID();
        this.ghostAvatars = new Vector<Sphere>();
    }
    
    @Override
    protected void processPacket(Object o) {
        String msg = (String)o;
        String msgTokens[] = msg.split(",");
        
        if (msgTokens.length > 0) {
            switch(msgTokens[0]) {
                case "join":
                    processJoin(msgTokens[1]);
                    break;
                case "bye":
                    processBye(msgTokens[1]);
                    break;
                case "dsfr":
                    processDsfr(UUID.fromString(msgTokens[1]), Float.parseFloat(msgTokens[2]), Float.parseFloat(msgTokens[3]), Float.parseFloat(msgTokens[4]));
                    break;
                case "wsds":
                    break;
                case "move":
                    //processMove(UUID.fromString(msgTokens[1]))
                    break;
                    
            }
        }
    }
    
    private void processJoin(String msg) {
        if ("success".equals(msg)) {
            //game.setIsConnected(true);
           // sendCreateMessage(game.getPlayerPosition());
        } else {
           // game.setIsConnected(false);
        }
    }
    
    private void processBye(String msg) {
        UUID ghostID = UUID.fromString(msg);
        //game.removeGhostAvatar(ghostID);
    }
    
    private void processDsfr(UUID ghostID, float x, float y, float z) {
        //game.createGhostAvatar(ghostID, x, y, z);
    }
    
    private void processMove(String msg) {
        
    }
    
    public void sendCreateMessage(Vector3D pos) {
        // format: create,localId,x,y,z
        try {
            String msg = new String("create," + id.toString());
            msg += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
            
            System.out.println("Sending create message: " + msg);
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendJoinMsg() {
        // format: join,localId
        try {
            String msg = new String("join," + id.toString());
            
            System.out.println("Sending join message: " + msg);
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendByeMessage() {
        try {
            String msg = new String("bye," + id.toString());
            
            System.out.println("Sending bye message: " + msg);
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendDetailsForMessage(UUID remId, Vector3D pos) {
        try {
            String msg = new String("dsfr," + id.toString());
            msg += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
            
            System.out.println("Sending DSFR message: " + msg);
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendMoveMsg(Vector3D pos) {
        try {
            String msg = new String("move," + id.toString());
            msg += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
            sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
