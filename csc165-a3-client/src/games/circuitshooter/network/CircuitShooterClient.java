package games.circuitshooter.network;

import games.circuitshooter.CircuitShooter;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import sage.networking.client.GameConnectionClient;

public class CircuitShooterClient extends GameConnectionClient {
    private UUID id;
    private ClientInputHandler input;
    private ClientOutputHandler output;
    
    public CircuitShooterClient(InetAddress remAddr, int remPort, ProtocolType pType, CircuitShooter game) throws IOException {
        super(remAddr, remPort, pType);
        
        this.id = UUID.randomUUID();
        this.output = new ClientOutputHandler(id, this);
        this.input = new ClientInputHandler(game, output);
    }
    
    @Override
    protected void processPacket(Object o) {
        String msg = (String)o;
        
        if (msg == null)
        	return;
        
        String msgTokens[] = msg.split(",");
        
        if (msgTokens.length > 0) {
            switch(msgTokens[0]) {
                case "join":
                    input.processJoin(msgTokens[1]);
                    break;
                    
                case "create":
                	input.processCreate(UUID.fromString(msgTokens[1]),
                				  Float.parseFloat(msgTokens[2]), 
                				  Float.parseFloat(msgTokens[3]), 
                				  Float.parseFloat(msgTokens[4]));
                	break;
                	
                case "bye":
                	input.processBye(UUID.fromString(msgTokens[1]));
                    break;
                    
                case "dsfr":
                	input.processDsfr(UUID.fromString(msgTokens[1]), 
                    		    Float.parseFloat(msgTokens[2]), 
                    		    Float.parseFloat(msgTokens[3]), 
                    		    Float.parseFloat(msgTokens[4]));
                    break;
                    
                case "wsds":
                	input.processWsds(UUID.fromString(msgTokens[1]));
                    break;
                    
                case "move":
                	input.processMove(UUID.fromString(msgTokens[1]), 
                    			Float.parseFloat(msgTokens[2]), 
                    			Float.parseFloat(msgTokens[3]), 
                    			Float.parseFloat(msgTokens[4]));
                    break;
                    
                case "rot":
                	input.processRotate(UUID.fromString(msgTokens[1]), 
                				  Float.parseFloat(msgTokens[2]), 
                				  Float.parseFloat(msgTokens[3]), 
                				  Float.parseFloat(msgTokens[4]), 
                				  Float.parseFloat(msgTokens[5]));
                	break;
                	
                case "proj":
                	input.processProjectile(UUID.fromString(msgTokens[1]));
                	break;
                	
                case "hit":
                	input.processHit(UUID.fromString(msgTokens[1]),
                			   UUID.fromString(msgTokens[2]), 
                			   Boolean.parseBoolean(msgTokens[2]));
                	break;
                	
                case "mnpc":
                	input.processMoveNPC(UUID.fromString(msgTokens[1]), 
                				   Float.parseFloat(msgTokens[2]), 
                				   Float.parseFloat(msgTokens[3]), 
                				   Float.parseFloat(msgTokens[4]));
            }
        }
    }
    
    public ClientOutputHandler getOutputHandler() {
    	return output;
    }
}
