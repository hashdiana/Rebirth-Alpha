//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.rebirthclient.api.events.impl;

import me.rebirthclient.api.events.Event;
import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent extends Event {
   public float partialTicks;
   public ScaledResolution scaledResolution;

   public Render2DEvent(float partialTicks, ScaledResolution scaledResolution) {
      this.partialTicks = partialTicks;
      this.scaledResolution = scaledResolution;
   }

   public void setPartialTicks(float partialTicks) {
      this.partialTicks = partialTicks;
   }

   public void setScaledResolution(ScaledResolution scaledResolution) {
      this.scaledResolution = scaledResolution;
   }

   public double getScreenWidth() {
      return this.scaledResolution.getScaledWidth_double();
   }

   public double getScreenHeight() {
      return this.scaledResolution.getScaledHeight_double();
   }
}
