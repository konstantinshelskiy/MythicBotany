package mythicbotany.pylon;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mythicbotany.base.BlockTE;
import mythicbotany.network.MythicNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.botania.api.BotaniaAPIClient;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.api.wand.IWandable;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.item.ItemManaTablet;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;

public class BlockAlfsteelPylon extends BlockTE<TileAlfsteelPylon> implements IWandHUD, IWandable {

    private static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 21.0D, 14.0D);

    public BlockAlfsteelPylon(Properties properties) {
        super(TileAlfsteelPylon.class, properties, new Item.Properties().setISTER(() -> RenderAlfsteelPylon.TEISR::new));
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext ctx) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    public BlockRenderType getRenderType(@Nonnull BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBlockClicked(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player) {
        if (!world.isRemote) {
            ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
            if (stack.getItem() instanceof PylonRepairable && ((PylonRepairable) stack.getItem()).canRepair(stack)) {
                ItemStack copy = stack.copy();
                player.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, copy);
                world.addEntity(entity);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult hit) {
        if (!world.isRemote) {
            ItemStack stack = player.getHeldItem(hand);
            if (stack.getItem() instanceof PylonRepairable && ((PylonRepairable) stack.getItem()).canRepair(stack)) {
                ItemStack copy = stack.copy();
                player.setHeldItem(hand, ItemStack.EMPTY);
                ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, copy);
                entity.setMotion(0, 0, 0);
                world.addEntity(entity);
                return ActionResultType.SUCCESS;
            } else {
                return super.onBlockActivated(state, world, pos, player, hand, hit);
            }
        } else {
            return super.onBlockActivated(state, world, pos, player, hand, hit);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHUD(MatrixStack matrixStack, Minecraft minecraft, World world, BlockPos pos) {
        TileAlfsteelPylon te = getTile(world, pos);
        int color = 4474111;
        BotaniaAPIClient.instance().drawSimpleManaHUD(matrixStack, color, te.getCurrentMana(), TileAlfsteelPylon.MAX_MANA, "Alfsteel Pylon");
    }

    @Override
    public boolean onUsedByWand(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Direction side) {
        if (world.isRemote) {
            MythicNetwork.requestTE(world, pos);
        }
        return true;
    }
}
