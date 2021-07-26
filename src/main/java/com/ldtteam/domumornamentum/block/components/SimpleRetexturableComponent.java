package com.ldtteam.domumornamentum.block.components;

import com.ldtteam.domumornamentum.block.IMateriallyTexturedBlockComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceLocation;

public class SimpleRetexturableComponent implements IMateriallyTexturedBlockComponent
{

    private final ResourceLocation id;
    private final Tag<Block> validSkins;
    private final Block defaultBlock;

    public SimpleRetexturableComponent(final ResourceLocation id, final Tag<Block> validSkins, final Block defaultBlock) {
        this.id = id;
        this.validSkins = validSkins;
        this.defaultBlock = defaultBlock;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public Tag<Block> getValidSkins()
    {
        return validSkins;
    }

    @Override
    public Block getDefault()
    {
        return defaultBlock;
    }
}
