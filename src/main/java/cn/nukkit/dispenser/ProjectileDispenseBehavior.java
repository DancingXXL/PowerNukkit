package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.Projectile;
import cn.nukkit.entity.impl.BaseEntity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.EntityRegistry;

/**
 * @author CreeperFace
 */
public class ProjectileDispenseBehavior implements DispenseBehavior {

    private EntityType<? extends Projectile> entityType;

    public ProjectileDispenseBehavior() {

    }

    public ProjectileDispenseBehavior(EntityType<? extends Projectile> entity) {
        this.entityType = entity;
    }

    @Override
    public void dispense(BlockDispenser source, Item item) {
        Position dispensePos = Position.fromObject(source.getDispensePosition(), source.getLevel());
        CompoundTag nbt = BaseEntity.getDefaultNBT(dispensePos);
        this.correctNBT(nbt);

        BlockFace face = source.getFacing();

        Projectile projectile = EntityRegistry.get().newEntity(entityType, dispensePos.getLevel().getChunk(dispensePos.getFloorX(), dispensePos.getFloorZ()), nbt);
        if (projectile == null) {
            return;
        }

        projectile.setMotion(new Vector3f(face.getXOffset(), face.getYOffset() + 0.1f, face.getZOffset()).multiply(6));
        projectile.spawnToAll();
    }

    protected EntityType<?> getEntityType() {
        return this.entityType;
    }

    /**
     * you can add extra data of projectile here
     *
     * @param nbt tag
     */
    protected void correctNBT(CompoundTag nbt) {

    }
}
