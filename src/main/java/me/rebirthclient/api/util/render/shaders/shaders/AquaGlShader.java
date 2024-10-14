//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.rebirthclient.api.util.render.shaders.shaders;

import me.rebirthclient.api.util.render.shaders.FramebufferShader;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class AquaGlShader extends FramebufferShader {
   private static AquaGlShader INSTANCE;
   public float time = 0.0F;

   public AquaGlShader() {
      super("aquaglow.frag");
   }

   public static FramebufferShader INSTANCE() {
      if (INSTANCE == null) {
         INSTANCE = new AquaGlShader();
      }

      return INSTANCE;
   }

   @Override
   public void setupUniforms() {
      this.setupUniform("resolution");
      this.setupUniform("time");
      this.setupUniform("texture");
      this.setupUniform("texelSize");
      this.setupUniform("color");
      this.setupUniform("divider");
      this.setupUniform("radius");
      this.setupUniform("maxSample");
   }

   @Override
   public void updateUniforms() {
      GL20.glUniform1f(this.getUniform("time"), this.time);
      GL20.glUniform2f(this.getUniform("resolution"), (float)new ScaledResolution(this.mc).getScaledWidth(), (float)new ScaledResolution(this.mc).getScaledHeight());
      GL20.glUniform1i(this.getUniform("texture"), 0);
      GL20.glUniform2f(
         this.getUniform("texelSize"),
         Float.intBitsToFloat(Float.floatToIntBits(1531.2186F) ^ 2067752703) / (float)this.mc.displayWidth * this.radius * this.quality,
         Float.intBitsToFloat(Float.floatToIntBits(103.132805F) ^ 2102281215) / (float)this.mc.displayHeight * this.radius * this.quality
      );
      GL20.glUniform3f(this.getUniform("color"), this.red, this.green, this.blue);
      GL20.glUniform1f(this.getUniform("divider"), this.divider);
      GL20.glUniform1f(this.getUniform("radius"), this.radius);
      GL20.glUniform1f(this.getUniform("maxSample"), this.maxSample);
      if (this.animation) {
         this.time = this.time > 100.0F ? 0.0F : (float)((double)this.time + 0.01 * (double)this.animationSpeed);
      }
   }
}
