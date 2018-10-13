package ca.ubc.cpen442.vpn.model;

public class Server {
    private int listeningPort; // The TCP port at which the server is listening.

    public Server(int listeningPort) {
        this.listeningPort = listeningPort;
    }
}
