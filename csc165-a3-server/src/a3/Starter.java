package a3;

import games.treasurehunt2015.TreasureHuntServer;

import java.io.IOException;

import sage.networking.IGameConnection.ProtocolType;

public class Starter {

    public static void main(String args[]) {
        try {
            TreasureHuntServer server = new TreasureHuntServer(0, ProtocolType.TCP);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
