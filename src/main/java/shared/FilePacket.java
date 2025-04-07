package shared;

import shared.Enum.ProtocolType;

import java.io.Serializable;

public class FilePacket implements Serializable {
    private static final long serialVersionUID = 1L;
    public String fileName;
    public ProtocolType protocol;

    public FilePacket(String fileName, ProtocolType protocol) {
        this.fileName = fileName;
        this.protocol = protocol;
    }
}
