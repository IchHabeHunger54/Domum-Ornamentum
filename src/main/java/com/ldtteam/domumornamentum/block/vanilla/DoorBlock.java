package com.ldtteam.domumornamentum.block.vanilla;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ldtteam.domumornamentum.block.AbstractBlockDoor;
import com.ldtteam.domumornamentum.block.ICachedItemGroupBlock;
import com.ldtteam.domumornamentum.block.IMateriallyTexturedBlock;
import com.ldtteam.domumornamentum.block.IMateriallyTexturedBlockComponent;
import com.ldtteam.domumornamentum.block.components.SimpleRetexturableComponent;
import com.ldtteam.domumornamentum.block.types.DoorType;
import com.ldtteam.domumornamentum.client.model.data.MaterialTextureData;
import com.ldtteam.domumornamentum.entity.block.MateriallyTexturedBlockEntity;
import com.ldtteam.domumornamentum.entity.block.ModBlockEntityTypes;
import com.ldtteam.domumornamentum.item.vanilla.DoorBlockItem;
import com.ldtteam.domumornamentum.tag.ModTags;
import com.ldtteam.domumornamentum.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.minecraft.world.level.block.Blocks.OAK_PLANKS;

@SuppressWarnings("deprecation")
public class DoorBlock extends AbstractBlockDoor<DoorBlock> implements IMateriallyTexturedBlock, ICachedItemGroupBlock, EntityBlock
{
    public static final EnumProperty<DoorType> TYPE = EnumProperty.create("type", DoorType.class);
    public static final List<IMateriallyTexturedBlockComponent> COMPONENTS = ImmutableList.<IMateriallyTexturedBlockComponent>builder()
                                                                               .add(new SimpleRetexturableComponent(new ResourceLocation("minecraft:block/oak_planks"), ModTags.SLAB_MATERIALS, OAK_PLANKS))
                                                                               .build();

    private final List<ItemStack> fillItemGroupCache = Lists.newArrayList();

    public DoorBlock()
    {
        super(Properties.of(Material.WOOD, MaterialColor.WOOD).strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn((state, blockGetter, pos, type) -> false));
        this.registerDefaultState(this.defaultBlockState().setValue(TYPE, DoorType.FULL));
        setRegistryName(com.ldtteam.domumornamentum.util.Constants.MOD_ID, "vanilla_doors_compat");
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.@NotNull Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(TYPE);
    }

    @Override
    public void registerItemBlock(final IForgeRegistry<Item> registry, final Item.Properties properties)
    {
        registry.register((new DoorBlockItem(this, properties)).setRegistryName(Objects.requireNonNull(this.getRegistryName())));
    }

    @Override
    public List<IMateriallyTexturedBlockComponent> getComponents()
    {
        return COMPONENTS;
    }

    @Override
    public void fillItemCategory(final @NotNull CreativeModeTab group, final @NotNull NonNullList<ItemStack> items)
    {
        if (!fillItemGroupCache.isEmpty()) {
            items.addAll(fillItemGroupCache);
            return;
        }

        IMateriallyTexturedBlockComponent materialComponent = getComponents().get(0);

        final Tag<Block> materialCandidate = materialComponent.getValidSkins();

        try {
            for (final DoorType DoorType : DoorType.values())
            {
                materialCandidate.getValues().forEach(cover -> {
                    final Map<ResourceLocation, Block> textureData = Maps.newHashMap();

                    textureData.put(materialComponent.getId(), cover);

                    final MaterialTextureData materialTextureData = new MaterialTextureData(textureData);

                    final CompoundTag textureNbt = materialTextureData.serializeNBT();

                    final ItemStack result = new ItemStack(this);
                    result.getOrCreateTag().put("textureData", textureNbt);
                    result.getOrCreateTag().putString("type", DoorType.toString().toUpperCase());

                    fillItemGroupCache.add(result);
                });
            }


        } catch (IllegalStateException exception)
        {
            //Ignored. Thrown during start up.
        }

        items.addAll(fillItemGroupCache);
    }

    @Override
    public void setPlacedBy(
      final @NotNull Level worldIn, final @NotNull BlockPos pos, final @NotNull BlockState state, @Nullable final LivingEntity placer, final @NotNull ItemStack stack)
    {
        super.setPlacedBy(worldIn, pos, state, Objects.requireNonNull(placer), stack);

        final String type = stack.getOrCreateTag().getString("type");
        worldIn.setBlock(
          pos,
          worldIn.getBlockState(pos).setValue(TYPE, DoorType.valueOf(type.toUpperCase())),
          Constants.BlockFlags.DEFAULT_AND_RERENDER
        );
        worldIn.setBlock(
          pos.above(),
          worldIn.getBlockState(pos.above()).setValue(TYPE, DoorType.valueOf(type.toUpperCase())),
          Constants.BlockFlags.DEFAULT_AND_RERENDER
        );

        final CompoundTag textureData = stack.getOrCreateTagElement("textureData");
        final BlockEntity lowerBlockEntity = worldIn.getBlockEntity(pos);

        if (lowerBlockEntity instanceof MateriallyTexturedBlockEntity)
            ((MateriallyTexturedBlockEntity) lowerBlockEntity).updateTextureDataWith(MaterialTextureData.deserializeFromNBT(textureData));

        final BlockEntity upperBlockEntity = worldIn.getBlockEntity(pos.above());

        if (upperBlockEntity instanceof MateriallyTexturedBlockEntity)
            ((MateriallyTexturedBlockEntity) upperBlockEntity).updateTextureDataWith(MaterialTextureData.deserializeFromNBT(textureData));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final @NotNull BlockPos blockPos, final @NotNull BlockState blockState)
    {
        return new MateriallyTexturedBlockEntity(ModBlockEntityTypes.MATERIALLY_TEXTURED, blockPos, blockState);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(final @NotNull BlockState state, final @NotNull LootContext.Builder builder)
    {
        return BlockUtils.getMaterializedItemStack(builder, (s, e) -> {
            s.getOrCreateTag().putString("type", e.getBlockState().getValue(TYPE).toString().toUpperCase());
            return s;
        });
    }

    @Override
    public ItemStack getPickBlock(
      final BlockState state, final HitResult target, final BlockGetter world, final BlockPos pos, final Player player)
    {
        return BlockUtils.getMaterializedItemStack(player, world, pos, (s, e) -> {
            s.getOrCreateTag().putString("type", e.getBlockState().getValue(TYPE).toString().toUpperCase());
            return s;
        });
    }

    @Override
    public void resetCache()
    {
        fillItemGroupCache.clear();
    }
}