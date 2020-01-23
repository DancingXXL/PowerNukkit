package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;

/**
 * Created by CreeperFace on 15.4.2017.
 */
public class BlockDispenser extends BlockSolid implements Faceable {

    public BlockDispenser(Identifier id) {
        super(id);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(id, 0);
    }

    @Override
    public int getComparatorInputOverride() {
        /*BlockEntity blockEntity = this.level.getBlockEntity(this);

        if(blockEntity instanceof BlockEntityDispenser) {
            //return ContainerInventory.calculateRedstone(((BlockEntityDispenser) blockEntity).getInventory()); TODO: dispenser
        }*/

        return super.getComparatorInputOverride();
    }

    public BlockFace getFacing() {
        return BlockFace.fromIndex(this.getDamage() & 7);
    }

    public boolean isTriggered() {
        return (this.getDamage() & 8) > 0;
    }

    public void setTriggered(boolean value) {
        int i = 0;
        i |= getFacing().getIndex();

        if (value) {
            i |= 8;
        }

        this.setDamage(i);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    public Vector3f getDispensePosition() {
        BlockFace facing = getFacing();
        double x = this.getX() + 0.7 * facing.getXOffset();
        double y = this.getY() + 0.7 * facing.getYOffset();
        double z = this.getZ() + 0.7 * facing.getZOffset();
        return new Vector3f(x, y, z);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x07);
    }
}
