//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.rebirthclient.api.util.shaders.impl.outline;

import java.awt.Color;
import java.util.HashMap;
import me.rebirthclient.api.util.shaders.FramebufferShader;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public final class CircleOutlineShader extends FramebufferShader {
   public float time = 0.0F;
   public static final CircleOutlineShader INSTANCE = new CircleOutlineShader();

   public void stopDraw(Color color, float f, float f2, boolean bl, int n, float f3, float f4, float f5) {
      this.mc.gameSettings.entityShadows = this.entityShadows;
      this.framebuffer.unbindFramebuffer();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      this.mc.getFramebuffer().bindFramebuffer(true);
      this.mc.entityRenderer.disableLightmap();
      RenderHelper.disableStandardItemLighting();
      this.startShader(color, f, f2, bl, n, f3, f4, f5);
      this.mc.entityRenderer.setupOverlayRendering();
      this.drawFramebuffer(this.framebuffer);
      this.stopShader();
      this.mc.entityRenderer.disableLightmap();
      GlStateManager.popMatrix();
      GlStateManager.popAttrib();
   }

   public void update(double d) {
      this.time = (float)((double)this.time + d);
   }

   public void startShader(Color color, float f, float f2, boolean bl, int n, float f3, float f4, float f5) {
      GL11.glPushMatrix();
      GL20.glUseProgram(this.program);
      if (this.uniformsMap == null) {
         this.uniformsMap = new HashMap<>();
         this.setupUniforms();
      }

      this.updateUniforms(color, f, f2, bl, n, f3, f4, f5);
   }

   public CircleOutlineShader() {
      super("circleOutline.frag");
   }

   @Override
   public void setupUniforms() {
      this.setupUniform("texture");
      this.setupUniform("texelSize");
      this.setupUniform("colors");
      this.setupUniform("divider");
      this.setupUniform("radius");
      this.setupUniform("maxSample");
      this.setupUniform("alpha0");
      this.setupUniform("resolution");
      this.setupUniform("time");
      this.setupUniform("PI");
      this.setupUniform("rad");
   }

   public void updateUniforms(Color color, float f, float f2, boolean bl, int n, float f3, float f4, float f5) {
      GL20.glUniform1i(this.getUniform("texture"), 0);
      GL20.glUniform2f(this.getUniform("texelSize"), 1.0F / (float)this.mc.displayWidth * f * f2, 1.0F / (float)this.mc.displayHeight * f * f2);
      GL20.glUniform3f(this.getUniform("colors"), (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F);
      GL20.glUniform1f(this.getUniform("divider"), 140.0F);
      GL20.glUniform1f(this.getUniform("radius"), f);
      GL20.glUniform1f(this.getUniform("maxSample"), 10.0F);
      GL20.glUniform1f(this.getUniform("alpha0"), bl ? -1.0F : (float)n / 255.0F);
      GL20.glUniform2f(
         this.getUniform("resolution"), (float)new ScaledResolution(this.mc).getScaledWidth() / f3, (float)new ScaledResolution(this.mc).getScaledHeight() / f3
      );
      GL20.glUniform1f(this.getUniform("time"), this.time);
      GL20.glUniform1f(this.getUniform("PI"), f4);
      GL20.glUniform1f(this.getUniform("rad"), f5);
   }
}
