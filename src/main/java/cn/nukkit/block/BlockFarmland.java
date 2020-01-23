package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3f;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.*;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFarmland extends BlockTransparent {

    public BlockFarmland(Identifier id) {
        super(id);
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.9375;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            Vector3f v = new Vector3f();

            if (this.level.getBlock(x, this.y + 1, z) instanceof BlockCrops) {
                return 0;
            }

            if (this.level.getBlock(x, this.y + 1, z).isSolid()) {
                this.level.setBlock(this, Block.get(DIRT), false, true);

                return Level.BLOCK_UPDATE_RANDOM;
            }

            boolean found = false;

            if (this.level.isRaining()) {
                found = true;
            } else {
                for (int x = this.x - 4; x <= this.x + 4; x++) {
                    for (int z = this.z - 4; z <= this.z + 4; z++) {
                        for (int y = this.y; y <= this.y + 1; y++) {
                            if (z == this.z && x == this.x && y == this.y) {
                                continue;
                            }

                            Identifier block = this.level.getBlockIdAt(x, y, z);

                            if (block == FLOWING_WATER || block == WATER) {
                                found = true;
                                break;
                            }
                        }
                    }
                }
            }

            Block block = this.level.getBlock(x, y - 1, z);
            if (found || block instanceof BlockWater) {
                if (this.getDamage() < 7) {
                    this.setDamage(7);
                    this.level.setBlock(this, this, false, false);
                }
                return Level.BLOCK_UPDATE_RANDOM;
            }

            if (this.getDamage() > 0) {
                this.setDamage(this.getDamage() - 1);
                this.level.setBlock(this, this, false, false);
            } else {
                this.level.setBlock(this, Block.get(DIRT), false, true);
            }

            return Level.BLOCK_UPDATE_RANDOM;
        }

        return 0;
    }

    @Override
    public Item toItem() {
        return Item.get(DIRT);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
