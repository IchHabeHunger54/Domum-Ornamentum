package com.ldtteam.domumornamentum.block;

import com.ldtteam.domumornamentum.util.Constants;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Class used to handle the creativeTab of structurize.
 */
public final class ModCreativeTabs
{
    public static final CreativeModeTab GENERAL = new CreativeModeTab(Constants.MOD_ID + ".general")
    {
        @Override
        public @NotNull ItemStack makeIcon()
        {
            return new ItemStack(ModBlocks.getArchitectsCutter());
        }

        @Override
        public boolean hasSearchBar()
        {
            return false;
        }
    };


    public static final CreativeModeTab TIMBER_FRAMES = new CreativeModeTab(Constants.MOD_ID + ".timber_frames")
    {
        @Override
        public @NotNull ItemStack makeIcon()
        {
            return new ItemStack(ModBlocks.getTimberFrames().stream().findFirst().orElse(null));
        }

        @Override
        public boolean hasSearchBar()
        {
            return false;
        }
    };

    public static final CreativeModeTab SHINGLES = new CreativeModeTab(Constants.MOD_ID + ".shingles")
    {
        @Override
        public @NotNull ItemStack makeIcon()
        {
            return new ItemStack(ModBlocks.getShingle());
        }

        @Override
        public boolean hasSearchBar()
        {
            return false;
        }
    };

    public static final CreativeModeTab SHINGLE_SLABS = new CreativeModeTab(Constants.MOD_ID + ".shingle_slabs")
    {
        @Override
        public @NotNull ItemStack makeIcon()
        {
            return new ItemStack(ModBlocks.getShingleSlab());
        }

        @Override
        public boolean hasSearchBar()
        {
            return false;
        }
    };

    public static final CreativeModeTab PAPERWALLS = new CreativeModeTab(Constants.MOD_ID + ".paperwalls")
    {
        @Override
        public @NotNull ItemStack makeIcon()
        {
            return new ItemStack(ModBlocks.getPaperWall());
        }

        @Override
        public boolean hasSearchBar()
        {
            return false;
        }
    };
}