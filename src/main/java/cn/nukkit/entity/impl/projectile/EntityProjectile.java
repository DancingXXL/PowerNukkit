package cn.nukkit.entity.impl.projectile;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBell;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.data.EntityData;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.entity.impl.EntityLiving;
import cn.nukkit.entity.misc.EnderCrystal;
import cn.nukkit.event.block.BellRingEvent;
import cn.nukkit.event.entity.EntityCombustByEntityEvent;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Set;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityProjectile extends BaseEntity {

    public BaseEntity shootingEntity;

    protected double getDamage() {
        return namedTag.contains("damage") ? namedTag.getDouble("damage") : getBaseDamage();
    }

    protected double getBaseDamage() {
        return 0;
    }

    public boolean hadCollision = false;

    public boolean closeOnCollide = true;

    protected double damage = 0;

    public EntityProjectile(EntityType<?> type, Chunk chunk, CompoundTag nbt) {
        this(type, chunk, nbt, null);
    }

    public EntityProjectile(EntityType<?> type, Chunk chunk, CompoundTag nbt, BaseEntity shootingEntity) {
        super(type, chunk, nbt);
        this.shootingEntity = shootingEntity;
        if (shootingEntity != null) {
            this.setLongData(EntityData.OWNER_EID, shootingEntity.getUniqueId());
        }
    }

    public int getResultDamage() {
        return NukkitMath.ceilDouble(Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ) * getDamage());
    }

    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    public void onCollideWithEntity(Entity entity) {
        this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)));
        float damage = this.getResultDamage();

        EntityDamageEvent ev;
        if (this.shootingEntity == null) {
            ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage);
        } else {
            ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage);
        }
        entity.attack(ev);
        addHitEffect();
        this.hadCollision = true;

        if (this.fireTicks > 0) {
            EntityCombustByEntityEvent event = new EntityCombustByEntityEvent(this, entity, 5);
            this.server.getPluginManager().callEvent(ev);
            if (!event.isCancelled()) {
                entity.setOnFire(event.getDuration());
            }
        }
        if (closeOnCollide) {
            this.close();
        }
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setMaxHealth(1);
        this.setHealth(1);
        if (this.namedTag.contains("Age")) {
            this.age = this.namedTag.getShort("Age");
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return (entity instanceof EntityLiving || entity instanceof EnderCrystal) && !this.onGround;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putShort("Age", this.age);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;
        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }
        this.lastUpdate = currentTick;

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {

            MovingObjectPosition movingObjectPosition = null;

            if (!this.isCollided) {
                this.motionY -= this.getGravity();
            }

            Vector3f moveVector = new Vector3f(this.x + this.motionX, this.y + this.motionY, this.z + this.motionZ);

            Set<Entity> collidingEntities = this.getLevel().getCollidingEntities(
                    this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1, 1, 1),
                    this);

            double nearDistance = Integer.MAX_VALUE;
            Entity nearEntity = null;

            for (Entity entity : collidingEntities) {
                if (/*!entity.canCollideWith(this) or */
                        (entity == this.shootingEntity && this.ticksLived < 5)
                        ) {
                    continue;
                }

                AxisAlignedBB axisalignedbb = entity.getBoundingBox().grow(0.3, 0.3, 0.3);
                MovingObjectPosition ob = axisalignedbb.calculateIntercept(this, moveVector);

                if (ob == null) {
                    continue;
                }

                double distance = this.distanceSquared(ob.hitVector);

                if (distance < nearDistance) {
                    nearDistance = distance;
                    nearEntity = entity;
                }
            }

            if (nearEntity != null) {
                movingObjectPosition = MovingObjectPosition.fromEntity(nearEntity);
            }

            if (movingObjectPosition != null) {
                if (movingObjectPosition.entityHit != null) {
                    onCollideWithEntity(movingObjectPosition.entityHit);
                    return true;
                }
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            if (this.isCollided && !this.hadCollision) { //collide with block
                this.hadCollision = true;

                this.motionX = 0;
                this.motionY = 0;
                this.motionZ = 0;

                this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromBlock(this.getFloorX(), this.getFloorY(), this.getFloorZ(), -1, this)));
                onCollideWithBlock();
                addHitEffect();
                return false;
            } else if (!this.isCollided && this.hadCollision) {
                this.hadCollision = false;
            }

            if (!this.hadCollision || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001) {
                double f = Math.sqrt((this.motionX * this.motionX) + (this.motionZ * this.motionZ));
                this.yaw = Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI;
                this.pitch = Math.atan2(this.motionY, f) * 180 / Math.PI;
                hasUpdate = true;
            }

            this.updateMovement();

        }

        return hasUpdate;
    }

    protected void onCollideWithBlock() {
        for (Block collisionBlock : level.getCollisionBlocks(getBoundingBox().grow(0.1, 0.1, 0.1))) {
            onCollideWithBlock(collisionBlock);
        }
    }

    protected boolean onCollideWithBlock(Block collisionBlock) {
        if (collisionBlock instanceof BlockBell) {
            ((BlockBell) collisionBlock).ring(this, BellRingEvent.RingCause.PROJECTILE);
            return true;
        }
        return false;
    }

    protected void addHitEffect() {

    }
}
