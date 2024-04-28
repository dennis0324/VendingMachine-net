package network;

import network.Payload;
import org.json.JSONException;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Processing {
    public abstract String run(Payload payload) throws SQLException, JSONException;
    protected Connection conn;
    protected String[] sqlData;
    protected Classification classification;
    protected String counter;

    public Processing(Connection conn, String[] sqlData, Classification classification) {
        this.conn = conn;
        this.sqlData = sqlData;
        this.classification = classification;
    }

    public Processing(Classification classification, String counter) { // handshake
        this.classification = classification;
        this.counter = counter;
    }
}

