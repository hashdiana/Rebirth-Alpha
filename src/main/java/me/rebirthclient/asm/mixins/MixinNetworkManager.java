package me.rebirthclient.asm.mixins;

import io.netty.channel.ChannelHandlerContext;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.mod.modules.impl.misc.NullPatcher;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NetworkManager.class})
public class MixinNetworkManager {
   @Inject(
      method = {"sendPacket(Lnet/minecraft/network/Packet;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSendPacketPre(Packet<?> packet, CallbackInfo info) {
      PacketEvent.Send event = new PacketEvent.Send(0, packet);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         info.cancel();
      }
   }

   @Inject(
      method = {"exceptionCaught"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable, CallbackInfo ci) {
      if (NullPatcher.INSTANCE.isOn()) {
         NullPatcher.INSTANCE.sendWarning(throwable);
         ci.cancel();
      }
   }

   @Inject(
      method = {"channelRead0"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onChannelReadPre(ChannelHandlerContext context, Packet<?> packet, CallbackInfo info) {
      PacketEvent.Receive event = new PacketEvent.Receive(0, packet);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         info.cancel();
      }
   }
}
