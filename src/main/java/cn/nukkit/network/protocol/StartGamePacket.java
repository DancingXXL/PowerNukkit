package cn.nukkit.network.protocol;

import cn.nukkit.level.gamerule.GameRuleMap;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.registry.ItemRegistry;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

/**
 * Created on 15-10-13.
 */
@Log4j2
@ToString
public class StartGamePacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.START_GAME_PACKET;

    public static final int GAME_PUBLISH_SETTING_NO_MULTI_PLAY = 0;
    public static final int GAME_PUBLISH_SETTING_INVITE_ONLY = 1;
    public static final int GAME_PUBLISH_SETTING_FRIENDS_ONLY = 2;
    public static final int GAME_PUBLISH_SETTING_FRIENDS_OF_FRIENDS = 3;
    public static final int GAME_PUBLISH_SETTING_PUBLIC = 4;

    public GameRuleMap gameRules;

    public long entityUniqueId;
    public long entityRuntimeId;
    public int playerGamemode;
    public float x;
    public float y;
    public float z;
    public float yaw;
    public float pitch;
    public int seed;
    public byte dimension;
    public int generator = 1;
    public int worldGamemode;
    public int difficulty;
    public int spawnX;
    public int spawnY;
    public int spawnZ;
    public boolean hasAchievementsDisabled = true;
    public int dayCycleStopTime = -1; //-1 = not stopped, any positive value = stopped at that time
    public int eduEditionOffer = 0;
    public boolean hasEduFeaturesEnabled = false;
    public float rainLevel;
    public float lightningLevel;
    public boolean hasConfirmedPlatformLockedContent = false;
    public boolean multiplayerGame = true;
    public boolean broadcastToLAN = true;
    public int xblBroadcastIntent = GAME_PUBLISH_SETTING_PUBLIC;
    public int platformBroadcastIntent = GAME_PUBLISH_SETTING_PUBLIC;
    public boolean commandsEnabled;
    public boolean isTexturePacksRequired = false;

    @Override
    public short pid() {
        return NETWORK_ID;
    }
    public boolean bonusChest = false;
    public boolean hasStartWithMapEnabled = false;
    public int permissionLevel = 1;
    public int serverChunkTickRange = 4;
    public boolean hasLockedBehaviorPack = false;
    public boolean hasLockedResourcePack = false;
    public boolean isFromLockedWorldTemplate = false;
    public boolean isUsingMsaGamertagsOnly = false;
    public boolean isFromWorldTemplate = false;
    public boolean isWorldTemplateOptionLocked = false;
    public boolean isOnlySpawningV1Villagers = false;
    public String vanillaVersion = "*";
    public String levelId = ""; //base64 string, usually the same as world folder name in vanilla
    public String worldName;
    public String premiumWorldTemplateId = "";
    public boolean isTrial = false;
    public boolean isMovementServerAuthoritative;
    public long currentTick;

    public int enchantmentSeed;

    public String multiplayerCorrelationId = "";

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityUniqueId(buffer, this.entityUniqueId);
        Binary.writeEntityRuntimeId(buffer, this.entityRuntimeId);
        Binary.writeVarInt(buffer, this.playerGamemode);
        Binary.writeVector3f(buffer, this.x, this.y, this.z);
        buffer.writeFloatLE(this.yaw);
        buffer.writeFloatLE(this.pitch);

        Binary.writeVarInt(buffer, this.seed);
        Binary.writeVarInt(buffer, this.dimension);
        Binary.writeVarInt(buffer, this.generator);
        Binary.writeVarInt(buffer, this.worldGamemode);
        Binary.writeVarInt(buffer, this.difficulty);
        Binary.writeBlockVector3(buffer, this.spawnX, this.spawnY, this.spawnZ);
        buffer.writeBoolean(this.hasAchievementsDisabled);
        Binary.writeVarInt(buffer, this.dayCycleStopTime);
        Binary.writeVarInt(buffer, this.eduEditionOffer);
        buffer.writeBoolean(this.hasEduFeaturesEnabled);
        buffer.writeFloatLE(this.rainLevel);
        buffer.writeFloatLE(this.lightningLevel);
        buffer.writeBoolean(this.hasConfirmedPlatformLockedContent);
        buffer.writeBoolean(this.multiplayerGame);
        buffer.writeBoolean(this.broadcastToLAN);
        Binary.writeVarInt(buffer, this.xblBroadcastIntent);
        Binary.writeVarInt(buffer, this.platformBroadcastIntent);
        buffer.writeBoolean(this.commandsEnabled);
        buffer.writeBoolean(this.isTexturePacksRequired);
        Binary.writeGameRules(buffer, this.gameRules);
        buffer.writeBoolean(this.bonusChest);
        buffer.writeBoolean(this.hasStartWithMapEnabled);
        Binary.writeVarInt(buffer, this.permissionLevel);
        buffer.writeIntLE(this.serverChunkTickRange);
        buffer.writeBoolean(this.hasLockedBehaviorPack);
        buffer.writeBoolean(this.hasLockedResourcePack);
        buffer.writeBoolean(this.isFromLockedWorldTemplate);
        buffer.writeBoolean(this.isUsingMsaGamertagsOnly);
        buffer.writeBoolean(this.isFromWorldTemplate);
        buffer.writeBoolean(this.isWorldTemplateOptionLocked);
        buffer.writeBoolean(this.isOnlySpawningV1Villagers);
        Binary.writeString(buffer, this.vanillaVersion);

        Binary.writeString(buffer, this.levelId);
        Binary.writeString(buffer, this.worldName);
        Binary.writeString(buffer, this.premiumWorldTemplateId);
        buffer.writeBoolean(this.isTrial);
        buffer.writeBoolean(this.isMovementServerAuthoritative);
        buffer.writeLongLE(this.currentTick);
        Binary.writeVarInt(buffer, this.enchantmentSeed);
        buffer.writeBytes(BlockRegistry.get().getCachedPalette());
        buffer.writeBytes(ItemRegistry.get().getCachedRuntimeItems());
        Binary.writeString(buffer, this.multiplayerCorrelationId);
    }

    private static class ItemData {
        private String name;
        private int id;
    }
}
