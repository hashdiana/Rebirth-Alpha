//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.rebirthclient.mod.modules.impl.player;

import me.rebirthclient.api.events.impl.MotionEvent;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Category;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.impl.combat.CrystalAura;
import me.rebirthclient.mod.modules.impl.combat.PacketExp;
import me.rebirthclient.mod.modules.settings.Setting;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TimerModule extends Module {
   public static TimerModule INSTANCE = new TimerModule();
   private final Setting<Float> tickNormal = this.add(new Setting<>("Speed", 1.2F, 0.1F, 10.0F));
   public final Setting<Boolean> disableWhenCrystal = this.add(new Setting<>("NoCrystal", true));
   public final Setting<Boolean> packetControl = this.add(new Setting<>("PacketControl", true));
   private final Timer packetListReset = new Timer();
   private int normalLookPos;
   private int rotationMode;
   private int normalPos;
   private float lastPitch;
   private float lastYaw;

   public TimerModule() {
      super("Timer", "Timer", Category.PLAYER);
      INSTANCE = this;
   }

   public static float nextFloat(float startInclusive, float endInclusive) {
      return startInclusive != endInclusive && !(endInclusive - startInclusive <= 0.0F)
         ? (float)((double)startInclusive + (double)(endInclusive - startInclusive) * Math.random())
         : startInclusive;
   }

   @SubscribeEvent
   public final void onPacketSend(PacketEvent.Send event) {
      if (!fullNullCheck()) {
         if (event.getPacket() instanceof Position && this.rotationMode == 1) {
            ++this.normalPos;
            if (this.normalPos > 20) {
               this.rotationMode = 2;
            }
         } else if (event.getPacket() instanceof PositionRotation && this.rotationMode == 2) {
            ++this.normalLookPos;
            if (this.normalLookPos > 20) {
               this.rotationMode = 1;
            }
         }
      }
   }

   @Override
   public void onDisable() {
      mc.timer.tickLength = 50.0F;
   }

   @Override
   public void onEnable() {
      mc.timer.tickLength = 50.0F;
      this.lastYaw = mc.player.rotationYaw;
      this.lastPitch = mc.player.rotationPitch;
      this.packetListReset.reset();
   }

   @Override
   public void onUpdate() {
      if (this.disableWhenCrystal.getValue() && CrystalAura.lastPos != null) {
         mc.timer.tickLength = 50.0F;
      } else {
         if (this.packetListReset.passedMs(1000L)) {
            this.normalPos = 0;
            this.normalLookPos = 0;
            this.rotationMode = 1;
            this.lastYaw = mc.player.rotationYaw;
            this.lastPitch = mc.player.rotationPitch;
            this.packetListReset.reset();
         }

         if (this.lastPitch > 85.0F) {
            this.lastPitch = 85.0F;
         }

         if (PacketExp.INSTANCE.isThrow() && PacketExp.INSTANCE.down.getValue()) {
            this.lastPitch = 85.0F;
         }

         mc.timer.tickLength = 50.0F / this.tickNormal.getValue();
      }
   }

   @SubscribeEvent
   public final void RotateEvent(MotionEvent event) {
      if (!this.disableWhenCrystal.getValue() || CrystalAura.lastPos == null) {
         if (this.packetControl.getValue()) {
            switch(this.rotationMode) {
               case 1:
                  event.setRotation(this.lastYaw, this.lastPitch);
                  break;
               case 2:
                  event.setRotation(this.lastYaw + nextFloat(1.0F, 3.0F), this.lastPitch + nextFloat(1.0F, 3.0F));
            }
         }
      }
   }

   @Override
   public String getInfo() {
      return String.valueOf(this.tickNormal.getValue());
   }
}
