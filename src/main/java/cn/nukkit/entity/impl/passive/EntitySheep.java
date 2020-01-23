package cn.nukkit.entity.impl.passive;

import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.passive.Sheep;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.player.Player;
import cn.nukkit.utils.DyeColor;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.BlockIds.WOOL;
import static cn.nukkit.entity.data.EntityData.COLOR;
import static cn.nukkit.entity.data.EntityFlag.SHEARED;
import static cn.nukkit.item.ItemIds.DYE;
import static cn.nukkit.item.ItemIds.SHEARS;

/**
 * Author: BeYkeRYkt Nukkit Project
 */
public class EntitySheep extends Animal implements Sheep {

    public static final int NETWORK_ID = 13;

    public boolean sheared = false;
    public int color = 0;

    public EntitySheep(EntityType<Sheep> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.65f;
        }
        return 1.3f;
    }

    @Override
    public String getName() {
        return "Sheep";
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(8);

        if (!this.namedTag.contains("Color")) {
            this.setColor(randomColor());
        } else {
            this.setColor(this.namedTag.getByte("Color"));
        }

        if (!this.namedTag.contains("Sheared")) {
            this.namedTag.putByte("Sheared", 0);
        } else {
            this.sheared = this.namedTag.getBoolean("Sheared");
        }

        this.setFlag(SHEARED, this.sheared);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putByte("Color", this.color);
        this.namedTag.putBoolean("Sheared", this.sheared);
    }

    @Override
    public boolean onInteract(Player player, Item item) {
        if (item.getId() == DYE) {
            this.setColor(((ItemDye) item).getDyeColor().getWoolData());
            return true;
        }

        return item.getId() == SHEARS && shear();
    }

    public boolean shear() {
        if (sheared) {
            return false;
        }

        this.sheared = true;
        this.setFlag(SHEARED, true);

        this.level.dropItem(this, Item.get(WOOL, getColor(), ThreadLocalRandom.current().nextInt(2) + 1));
        return true;
    }

    @Override
    public Item[] getDrops() {
        if (this.lastDamageCause instanceof EntityDamageByEntityEvent) {
            return new Item[]{Item.get(WOOL, getColor(), 1)};
        }
        return new Item[0];
    }

    public int getColor() {
        return namedTag.getByte("Color");
    }

    public void setColor(int color) {
        this.color = color;
        this.setByteData(COLOR, color);
        this.namedTag.putByte("Color", this.color);
    }

    private int randomColor() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double rand = random.nextDouble(1, 100);

        if (rand <= 0.164) {
            return DyeColor.PINK.getWoolData();
        }

        if (rand <= 15) {
            return random.nextBoolean() ? DyeColor.BLACK.getWoolData() : random.nextBoolean() ? DyeColor.GRAY.getWoolData() : DyeColor.LIGHT_GRAY.getWoolData();
        }

        return DyeColor.WHITE.getWoolData();
    }
}
