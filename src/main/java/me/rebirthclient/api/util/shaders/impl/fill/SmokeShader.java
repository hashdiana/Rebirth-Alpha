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

public class SmokeShader extends FramebufferShader {
   public float time;
   public static final SmokeShader INSTANCE = new SmokeShader();

   public void startShader(float f, Color color, Color color2, Color color3, int n) {
      GL11.glPushMatrix();
      GL20.glUseProgram(this.program);
      if (this.uniformsMap == null) {
         this.uniformsMap = new HashMap<>();
         this.setupUniforms();
      }

      this.updateUniforms(f, color, color2, color3, n);
   }

   public void updateUniforms(float f, Color color, Color color2, Color color3, int n) {
      GL20.glUniform2f(
         this.getUniform("resolution"), (float)new ScaledResolution(this.mc).getScaledWidth() / f, (float)new ScaledResolution(this.mc).getScaledHeight() / f
      );
      GL20.glUniform1f(this.getUniform("time"), this.time);
      GL20.glUniform4f(
         this.getUniform("first"),
         (float)color.getRed() / 255.0F * 5.0F,
         (float)color.getGreen() / 255.0F * 5.0F,
         (float)color.getBlue() / 255.0F * 5.0F,
         (float)color.getAlpha() / 255.0F
      );
      GL20.glUniform3f(
         this.getUniform("second"), (float)color2.getRed() / 255.0F * 5.0F, (float)color2.getGreen() / 255.0F * 5.0F, (float)color2.getBlue() / 255.0F * 5.0F
      );
      GL20.glUniform3f(
         this.getUniform("third"), (float)color3.getRed() / 255.0F * 5.0F, (float)color3.getGreen() / 255.0F * 5.0F, (float)color3.getBlue() / 255.0F * 5.0F
      );
      GL20.glUniform1i(this.getUniform("oct"), n);
   }

   @Override
   public void setupUniforms() {
      this.setupUniform("resolution");
      this.setupUniform("time");
      this.setupUniform("first");
      this.setupUniform("second");
      this.setupUniform("third");
      this.setupUniform("oct");
   }

   public void update(double d) {
      this.time = (float)((double)this.time + d);
   }

   public SmokeShader() {
      super("smoke.frag");
   }

   public void stopDraw(Color color, float f, float f2, float f3, Color color2, Color color3, Color color4, int n) {
      this.mc.gameSettings.entityShadows = this.entityShadows;
      this.framebuffer.unbindFramebuffer();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      this.mc.getFramebuffer().bindFramebuffer(true);
      this.red = (float)color.getRed() / 255.0F;
      this.green = (float)color.getGreen() / 255.0F;
      this.blue = (float)color.getBlue() / 255.0F;
      this.alpha = (float)color.getAlpha() / 255.0F;
      this.radius = f;
      this.quality = f2;
      this.mc.entityRenderer.disableLightmap();
      RenderHelper.disableStandardItemLighting();
      this.startShader(f3, color2, color3, color4, n);
      this.mc.entityRenderer.setupOverlayRendering();
      this.drawFramebuffer(this.framebuffer);
      this.stopShader();
      this.mc.entityRenderer.disableLightmap();
      GlStateManager.popMatrix();
      GlStateManager.popAttrib();
   }
}
