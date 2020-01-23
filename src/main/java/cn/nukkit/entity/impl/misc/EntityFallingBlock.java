package cn.nukkit.entity.impl.misc;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLava;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.misc.FallingBlock;
import cn.nukkit.event.entity.EntityBlockChangeEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.gamerule.GameRules;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.Vector3f;
import cn.nukkit.math.Vector3i;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.BlockRegistry;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.block.BlockIds.ANVIL;
import static cn.nukkit.entity.data.EntityData.VARIANT;

/**
 * @author MagicDroidX
 */
public class EntityFallingBlock extends BaseEntity implements FallingBlock {

    protected int blockId;
    protected int damage;
    protected boolean breakOnLava;

    public EntityFallingBlock(EntityType<FallingBlock> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.98f;
    }

    @Override
    public float getLength() {
        return 0.98f;
    }

    @Override
    public float getHeight() {
        return 0.98f;
    }

    @Override
    public float getGravity() {
        return 0.04f;
    }

    @Override
    public float getDrag() {
        return 0.02f;
    }

    @Override
    protected float getBaseOffset() {
        return 0.49f;
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (namedTag != null) {
            if (namedTag.contains("TileID")) {
                blockId = namedTag.getInt("TileID");
            } else if (namedTag.contains("Tile")) {
                blockId = namedTag.getInt("Tile");
                namedTag.putInt("TileID", blockId);
            }

            if (namedTag.contains("Data")) {
                damage = namedTag.getByte("Data");
            }
        }

        breakOnLava = namedTag.getBoolean("BreakOnLava");

        if (blockId == 0) {
            close();
            return;
        }

        setIntData(VARIANT, BlockRegistry.get().getRuntimeId(this.getBlock(), this.getDamage()));
    }

    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    @Override
    public boolean onUpdate(int currentTick) {

        if (closed) {
            return false;
        }

        this.timing.startTiming();

        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0 && !justCreated) {
            return true;
        }

        lastUpdate = currentTick;

        boolean hasUpdate = entityBaseTick(tickDiff);

        if (isAlive()) {
            motionY -= getGravity();

            move(motionX, motionY, motionZ);

            float friction = 1 - getDrag();

            motionX *= friction;
            motionY *= 1 - getDrag();
            motionZ *= friction;

            Vector3i pos = (new Vector3f(x - 0.5, y, z - 0.5)).round().asVector3i();

            if (breakOnLava && level.getBlock(pos.subtract(0, 1, 0)) instanceof BlockLava) {
                close();
                if (this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                    getLevel().dropItem(this, Block.get(this.getBlock(), this.getDamage()).toItem());
                }
                level.addParticle(new DestroyBlockParticle(pos, Block.get(getBlock(), getDamage())));
                return true;
            }

            if (onGround) {
                close();
                Block block = level.getBlock(pos);
                if (block.getId() != AIR && block.isTransparent() && !block.canBeReplaced()) {
                    if (this.level.getGameRules().get(GameRules.DO_ENTITY_DROPS)) {
                        getLevel().dropItem(this, Block.get(this.getBlock(), this.getDamage()).toItem());
                    }
                } else {
                    EntityBlockChangeEvent event = new EntityBlockChangeEvent(this, block, Block.get(getBlock(), getDamage()));
                    server.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        getLevel().setBlock(pos, event.getTo(), true);

                        if (event.getTo().getId() == ANVIL) {
                            getLevel().addSound(pos.asVector3f(), Sound.RANDOM_ANVIL_LAND);
                        }
                    }
                }
                hasUpdate = true;
            }

            updateMovement();
        }

        this.timing.stopTiming();

        return hasUpdate || !onGround || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001;
    }

    public int getBlock() {
        return blockId;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void saveNBT() {
        namedTag.putInt("TileID", blockId);
        namedTag.putByte("Data", damage);
    }

    @Override
    public boolean canBeMovedByCurrents() {
        return false;
    }
}
