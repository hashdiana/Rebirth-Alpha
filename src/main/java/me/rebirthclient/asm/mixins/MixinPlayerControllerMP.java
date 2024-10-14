//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.rebirthclient.asm.mixins;

import me.rebirthclient.api.events.impl.BlockEvent;
import me.rebirthclient.api.events.impl.BreakBlockEvent;
import me.rebirthclient.api.events.impl.RightClickBlockEvent;
import me.rebirthclient.api.managers.Managers;
import me.rebirthclient.mod.modules.impl.player.TpsSync;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlayerControllerMP.class})
public class MixinPlayerControllerMP {
   @Redirect(
      method = {"onPlayerDamageBlock"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/block/state/IBlockState;getPlayerRelativeBlockHardness(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)F"
)
   )
   public float getPlayerRelativeBlockHardnessHook(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos) {
      return state.getPlayerRelativeBlockHardness(player, worldIn, pos)
         * (TpsSync.INSTANCE.isOn() && TpsSync.INSTANCE.mining.getValue() ? 1.0F / Managers.SERVER.getTpsFactor() : 1.0F);
   }

   @Inject(
      method = {"processRightClickBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void processRightClickBlock(
      EntityPlayerSP player, WorldClient worldIn, BlockPos pos, EnumFacing direction, Vec3d vec, EnumHand hand, CallbackInfoReturnable<EnumActionResult> cir
   ) {
      RightClickBlockEvent event = new RightClickBlockEvent(pos, hand, Minecraft.instance.player.getHeldItem(hand));
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         cir.cancel();
      }
   }

   @Inject(
      method = {"onPlayerDestroyBlock"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/world/World;playEvent(ILnet/minecraft/util/math/BlockPos;I)V"
)},
      cancellable = true
   )
   private void onPlayerDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
      MinecraftForge.EVENT_BUS.post(new BreakBlockEvent(pos));
   }

   @Inject(
      method = {"clickBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void clickBlockHook(BlockPos pos, EnumFacing face, CallbackInfoReturnable<Boolean> info) {
      BlockEvent event = new BlockEvent(pos, face);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         info.setReturnValue(false);
      }
   }

   @Inject(
      method = {"onPlayerDamageBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onPlayerDamageBlockHook(BlockPos pos, EnumFacing face, CallbackInfoReturnable<Boolean> info) {
      BlockEvent event = new BlockEvent(pos, face);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         info.setReturnValue(false);
      }
   }
}
