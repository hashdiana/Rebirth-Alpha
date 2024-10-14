//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.rebirthclient.api.util.shaders.impl.fill;

import java.awt.Color;
import java.util.HashMap;
import me.rebirthclient.api.util.shaders.FramebufferShader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class CircleShader extends FramebufferShader {
   public static final CircleShader INSTANCE = new CircleShader();
   public float time;

   public void startShader(float f, Color color, Float f2, Float f3) {
      GL11.glPushMatrix();
      GL20.glUseProgram(this.program);
      if (this.uniformsMap == null) {
         this.uniformsMap = new HashMap<>();
         this.setupUniforms();
      }

      this.updateUniforms(f, color, f2, f3);
   }

   public void update(double d) {
      this.time = (float)((double)this.time + d);
   }

   public void stopDraw(float f, Color color, Float f2, Float f3) {
      this.mc.gameSettings.entityShadows = this.entityShadows;
      this.framebuffer.unbindFramebuffer();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      this.mc.getFramebuffer().bindFramebuffer(true);
      this.mc.entityRenderer.disableLightmap();
      RenderHelper.disableStandardItemLighting();
      this.startShader(f, color, f2, f3);
      this.mc.entityRenderer.setupOverlayRendering();
      this.drawFramebuffer(this.framebuffer);
      this.stopShader();
      this.mc.entityRenderer.disableLightmap();
      GlStateManager.popMatrix();
      GlStateManager.popAttrib();
   }

   @Override
   public void setupUniforms() {
      this.setupUniform("resolution");
      this.setupUniform("time");
      this.setupUniform("colors");
      this.setupUniform("PI");
      this.setupUniform("rad");
   }

   public CircleShader() {
      super("circle.frag");
   }

   public void updateUniforms(float f, Color color, Float f2, Float f3) {
      GL20.glUniform2f(
         this.getUniform("resolution"), (float)new ScaledResolution(this.mc).getScaledWidth() / f, (float)new ScaledResolution(this.mc).getScaledHeight() / f
      );
      GL20.glUniform1f(this.getUniform("time"), this.time);
      GL20.glUniform1f(this.getUniform("PI"), f2);
      GL20.glUniform1f(this.getUniform("rad"), f3);
      GL20.glUniform4f(
         this.getUniform("colors"),
         (float)color.getRed() / 255.0F,
         (float)color.getGreen() / 255.0F,
         (float)color.getBlue() / 255.0F,
         (float)color.getAlpha() / 255.0F
      );
   }
}
