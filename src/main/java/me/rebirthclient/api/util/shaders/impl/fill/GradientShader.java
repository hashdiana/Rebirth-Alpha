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

public class GradientShader extends FramebufferShader {
   public static final GradientShader INSTANCE = new GradientShader();
   public float time;

   public GradientShader() {
      super("gradient.frag");
   }

   public void update(double d) {
      this.time = (float)((double)this.time + d);
   }

   public void stopDraw(Color color, float f, float f2, float f3, float f4, float f5, float f6, int n) {
      this.mc.gameSettings.entityShadows = this.entityShadows;
      this.framebuffer.unbindFramebuffer();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      this.mc.getFramebuffer().bindFramebuffer(true);
      this.red = (float)color.getRed() / 255.0F;
      this.green = (float)color.getGreen() / 255.0F;
      this.blue = (float)color.getBlue() / 255.0F;
      this.radius = f;
      this.quality = f2;
      this.mc.entityRenderer.disableLightmap();
      RenderHelper.disableStandardItemLighting();
      this.startShader(f3, f4, f5, f6, n);
      this.mc.entityRenderer.setupOverlayRendering();
      this.drawFramebuffer(this.framebuffer);
      this.stopShader();
      this.mc.entityRenderer.disableLightmap();
      GlStateManager.popMatrix();
      GlStateManager.popAttrib();
   }

   public void startShader(float f, float f2, float f3, float f4, int n) {
      GL11.glPushMatrix();
      GL20.glUseProgram(this.program);
      if (this.uniformsMap == null) {
         this.uniformsMap = new HashMap<>();
         this.setupUniforms();
      }

      this.updateUniforms(f, f2, f3, f4, n);
   }

   public void updateUniforms(float f, float f2, float f3, float f4, int n) {
      GL20.glUniform2f(
         this.getUniform("resolution"), (float)new ScaledResolution(this.mc).getScaledWidth() / f, (float)new ScaledResolution(this.mc).getScaledHeight() / f
      );
      GL20.glUniform1f(this.getUniform("time"), this.time);
      GL20.glUniform1f(this.getUniform("moreGradient"), f2);
      GL20.glUniform1f(this.getUniform("Creepy"), f3);
      GL20.glUniform1f(this.getUniform("alpha"), f4);
      GL20.glUniform1i(this.getUniform("NUM_OCTAVES"), n);
   }

   @Override
   public void setupUniforms() {
      this.setupUniform("resolution");
      this.setupUniform("time");
      this.setupUniform("moreGradient");
      this.setupUniform("Creepy");
      this.setupUniform("alpha");
      this.setupUniform("NUM_OCTAVES");
   }
}
