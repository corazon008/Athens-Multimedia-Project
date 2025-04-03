package com.streaming.server;

import java.io.Serializable;

public class Packet implements Serializable {
    private static final long serialVersionUID = 1L;
    int packetId;
    public boolean isLastPacket = false;

    public Packet(int packetId) {
        this.packetId = packetId;
    }

    public String toString() {
        return "Packet n°" + packetId;
    }
}
