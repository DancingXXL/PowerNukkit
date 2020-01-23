package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

@ToString(exclude = "sha256")
public class ResourcePackDataInfoPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.RESOURCE_PACK_DATA_INFO_PACKET;

    public static final int TYPE_INVALID = 0;
    public static final int TYPE_ADDON = 1;
    public static final int TYPE_CACHED = 2;
    public static final int TYPE_COPY_PROTECTED = 3;
    public static final int TYPE_BEHAVIOR = 4;
    public static final int TYPE_PERSONA_PIECE = 5;
    public static final int TYPE_RESOURCE = 6;
    public static final int TYPE_SKINS = 7;
    public static final int TYPE_WORLD_TEMPLATE = 8;

    public String packId;
    public int maxChunkSize;
    public int chunkCount;
    public long compressedPackSize;
    public byte[] sha256;
    public boolean premium;
    public int type = TYPE_RESOURCE;

    @Override
    protected void decode(ByteBuf buffer) {
        this.packId = Binary.readString(buffer);
        this.maxChunkSize = buffer.readIntLE();
        this.chunkCount = buffer.readIntLE();
        this.compressedPackSize = buffer.readLongLE();
        this.sha256 = Binary.readByteArray(buffer);
        this.premium = buffer.readBoolean();
        this.type = buffer.readByte();
    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeString(buffer, this.packId);
        buffer.writeIntLE(this.maxChunkSize);
        buffer.writeIntLE(this.chunkCount);
        buffer.writeLongLE(this.compressedPackSize);
        Binary.writeByteArray(buffer, this.sha256);
        buffer.writeBoolean(this.premium);
        buffer.writeByte((byte) this.type);
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }
}
