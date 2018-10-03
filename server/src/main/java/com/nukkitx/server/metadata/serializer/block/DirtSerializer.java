package com.nukkitx.server.metadata.serializer.block;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.block.Dirt;
import com.nukkitx.server.metadata.serializer.Serializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author CreeperFace
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class DirtSerializer implements Serializer<Dirt> {

    public static final Serializer INSTANCE = new DirtSerializer();

    @Override
    public short readMetadata(Dirt data) {
        return (short) data.ordinal();
    }

    @Override
    public Dirt writeMetadata(ItemType type, short metadata) {
        return Dirt.values()[GenericMath.clamp(metadata, 0, 2)];
    }
}