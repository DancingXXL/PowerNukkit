package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.SmokeParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

import java.util.ArrayDeque;
import java.util.Queue;

import static cn.nukkit.block.BlockIds.*;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockSponge extends BlockSolid {

    public static final int DRY = 0;
    public static final int WET = 1;

    public BlockSponge(Identifier id) {
        super(id);
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        Level level = block.getLevel();
        boolean blockSet = level.setBlock(block, this);

        if (blockSet) {
            if (this.getDamage() == WET && level.getDimension() == Level.DIMENSION_NETHER) {
                level.setBlock(block, Block.get(SPONGE, DRY));
                this.getLevel().addSound(block.asVector3f(), Sound.RANDOM_FIZZ);

                for (int i = 0; i < 8; ++i) {
                    this.getLevel().addParticle(
                            //TODO: Use correct smoke particle
                            new SmokeParticle(block.asVector3f().add(Math.random(), 1, Math.random())));
                }
            } else if (this.getDamage() == DRY && performWaterAbsorb(block)) {
                level.setBlock(block, Block.get(SPONGE, WET));

                for (int i = 0; i < 4; i++) {
                    LevelEventPacket packet = new LevelEventPacket();
                    packet.evid = 2001;
                    packet.x = (float) block.getX();
                    packet.y = (float) block.getY();
                    packet.z = (float) block.getZ();
                    packet.data = BlockRegistry.get().getRuntimeId(FLOWING_WATER, 0);
                    level.addChunkPacket(getChunkX(), getChunkZ(), packet);
                }
            }
        }
        return blockSet;
    }

    private boolean performWaterAbsorb(Block block) {
        Queue<Entry> entries = new ArrayDeque<>();

        entries.add(new Entry(block, 0));

        Entry entry;
        int waterRemoved = 0;
        while (waterRemoved < 64 && (entry = entries.poll()) != null) {
            for (BlockFace face : BlockFace.values()) {

                Block faceBlock = entry.block.getSide(face);
                if (faceBlock.getId() == FLOWING_WATER || faceBlock.getId() == WATER) {
                    this.level.setBlock(faceBlock, Block.get(AIR, 0));
                    ++waterRemoved;
                    if (entry.distance < 6) {
                        entries.add(new Entry(faceBlock, entry.distance + 1));
                    }
                } else if (faceBlock.getId() == AIR) {
                    if (entry.distance < 6) {
                        entries.add(new Entry(faceBlock, entry.distance + 1));
                    }
                }
            }
        }
        return waterRemoved > 0;
    }

    private static class Entry {
        private final Block block;
        private final int distance;

        public Entry(Block block, int distance) {
            this.block = block;
            this.distance = distance;
        }
    }
}
