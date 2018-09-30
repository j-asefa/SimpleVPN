package ca.ubc.cpen442.vpn;

/**
 * A Singleton that handles all network operations.
 */
public class NetworkBrain {
    private static NetworkBrain instance = null;

    protected NetworkBrain() {
        // Defeats instantiation.
    }

    public static NetworkBrain getInstance() {
        if (instance == null) {
            instance = new NetworkBrain();
        }
        return instance;
    }
}
