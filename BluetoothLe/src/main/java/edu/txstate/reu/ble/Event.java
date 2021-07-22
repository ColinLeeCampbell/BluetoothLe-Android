package edu.txstate.reu.ble;

import java.nio.ByteBuffer;
import java.sql.Timestamp;

public class Event {

    protected String sourceNodeId = "";
    protected String path = "";
    protected float[] data;
    protected Timestamp timestamp;

    public Event (byte[] data, Timestamp timestamp) {
        this.data = cleanData(data);
        this.timestamp = timestamp;
    }
    private float[] cleanData(byte[] data) {

        ByteBuffer buffer = ByteBuffer.wrap(data);

        float[] result = new float[data.length / 4];
        for (int i=0; i < result.length; i++) {
            result[i] = buffer.getFloat();
        }

        return result;
    }

    public String getSourceNodeId() { return sourceNodeId; }

    public String getPath() { return path; }

    public float[] getData() { return data; }

    public Timestamp getTimestamp() { return timestamp; }
}
