package dev.ultreon.devices.block;

//import dev.architectury.registry.block.BlockProperties;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.Gui;
//import net.minecraft.client.gui.screens.Screen;
//import net.minecraft.client.resources.language.I18n;
//import net.minecraft.network.chat.TranslatableComponent;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.TooltipFlag;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.state.BlockBehaviour;
//import net.minecraft.world.level.material.Material;
//import net.minecraft.world.level.material.MaterialColor;
//import net.minecraft.world.phys.AABB;
//
//import org.jetbrains.annotations.Nullable;
//import java.util.List;

public class DigitalClockBlock{// extends Block {
//    private static final AABB COLLISION_BOXES = new AABB(6, 0, 4, 9, 4, 12);
//    private static final AABB SELECTION_BOXES = new AABB(5, 0, 3, 10, 5, 13);
//
//    @SuppressWarnings({"deprecation", "ScheduledForRemoval"})
//    public DigitalClockBlock()
//    {
//        super(BlockBehaviour.Properties.of(Material.WOOD).lightLevel(value -> 8).strength(15, 15));
//    }
//
//
////    public static void.json addInformation(ItemStack stack, @Nullable Level player, List<Compon> tooltip, TooltipFlag advanced)
////    {
////        if(Screen.hasShiftDown())
////        {
////            tooltip.addAll(Minecraft.getInstance().font.split(new TranslatableComponent("devices.digital_clock.info"), 150));
////        }
////        else
////        {
////            tooltip.add( + I18n.format("cfm.info"));
////        }
////    }
//
//    @Override
//    public void.json addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
//    {
//        Block.addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_BOXES[state.getValue(FACING).getHorizontalIndex()]);
//    }
//
//    @Override
//    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
//    {
//        return SELECTION_BOXES[state.getValue(FACING).getHorizontalIndex()];
//    }
//
//    @Override
//    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
//    {
//        TileEntity tileEntity = worldIn.getTileEntity(pos);
//        if(tileEntity instanceof IColored)
//        {
//            IColored colored = (IColored) tileEntity;
//            state = state.withProperty(BlockColored.COLOR, colored.getColor());
//        }
//        return state;
//    }
//
//    @Override
//    public void.json harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity tileEntity, ItemStack stack)
//    {
//        if(tileEntity instanceof TileEntityDigitalClock)
//        {
//            TileEntityDigitalClock clock = (TileEntityDigitalClock) tileEntity;
//            ItemStack itemstack = new ItemStack(this, 1, clock.getColor().getMetadata());
//            spawnAsEntity(worldIn, pos, itemstack);
//        }
//        else
//        {
//            super.harvestBlock(worldIn, player, pos, state, tileEntity, stack);
//        }
//    }
//
//    @Override
//    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
//    {
//        TileEntity tileEntity = world.getTileEntity(pos);
//        if(tileEntity instanceof IColored)
//        {
//            return new ItemStack(Item.getItemFromBlock(this), 1, ((IColored) tileEntity).getColor().getMetadata());
//        }
//        return super.getPickBlock(state, target, world, pos, player);
//    }
//
//    @Override
//    public void.json onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
//    {
//        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
//        TileEntity tileEntity = worldIn.getTileEntity(pos);
//        if(tileEntity instanceof IColored)
//        {
//            IColored colored = (IColored) tileEntity;
//            colored.setColor(EnumDyeColor.byMetadata(stack.getMetadata()));
//        }
//    }
//
//    @Override
//    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
//    {
//        if(!worldIn.isRemote)
//        {
//            ItemStack heldItem = playerIn.getHeldItem(hand);
//            if(!heldItem.isEmpty() && heldItem.getItem() instanceof ItemDye)
//            {
//                TileEntity tileEntity = worldIn.getTileEntity(pos);
//                if(tileEntity instanceof TileEntityDigitalClock)
//                {
//                    TileEntityDigitalClock digitalClock = (TileEntityDigitalClock) tileEntity;
//                    digitalClock.setTextColorColor(EnumDyeColor.byDyeDamage(heldItem.getMetadata()));
//                    if(!playerIn.capabilities.isCreativeMode)
//                    {
//                        heldItem.shrink(1);
//                    }
//                }
//            }
//        }
//        return true;
//    }
//
//    @Override
//    protected BlockStateContainer createBlockState()
//    {
//        return new BlockStateContainer(this, FACING, BlockColored.COLOR);
//    }
//
//    @Nullable
//    @Override
//    public TileEntity createNewTileEntity(World worldIn, int meta)
//    {
//        return new TileEntityDigitalClock();
//    }
}
