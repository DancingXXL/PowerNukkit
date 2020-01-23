package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.math.BedrockRandom;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Niall Lindsay (Niall7459)
 * <p>
 * Nukkit Project
 * </p>
 */

public class PopulatorTallSugarcane extends PopulatorSugarcane {
    @Override
    protected void placeBlock(int x, int y, int z, Block block, IChunk chunk, BedrockRandom random) {
        int height = ThreadLocalRandom.current().nextInt(3) + 1;
        for (int i = 0; i < height; i++) {
            chunk.setBlock(x, y + i, z, block);
        }
    }
}
