/*
 * LiquidBounce++ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/PlusPlusMC/LiquidBouncePlusPlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.block;

import de.enzaxd.viaforge.ViaForge;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockFarmland.class)
public abstract class MixinBlockFarmland extends MixinBlock{
    @Overwrite
    public AxisAlignedBB getCollisionBoundingBox(World p_getCollisionBoundingBox_1_, BlockPos p_getCollisionBoundingBox_2_, IBlockState p_getCollisionBoundingBox_3_) {
        double f = ViaForge.getInstance().getVersion() <= 47 ? 1.0 : 0.9375;
        return new AxisAlignedBB((double)p_getCollisionBoundingBox_2_.getX(), (double)p_getCollisionBoundingBox_2_.getY(), (double)p_getCollisionBoundingBox_2_.getZ(), (double)(p_getCollisionBoundingBox_2_.getX() + 1), (double)p_getCollisionBoundingBox_2_.getY() + f, (double)(p_getCollisionBoundingBox_2_.getZ() + 1));


    }
}