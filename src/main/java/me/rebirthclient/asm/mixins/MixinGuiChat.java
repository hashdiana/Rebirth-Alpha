//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.rebirthclient.asm.mixins;

import me.rebirthclient.api.managers.Managers;
import me.rebirthclient.api.util.Wrapper;
import me.rebirthclient.api.util.render.RenderUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiChat.class})
public abstract class MixinGuiChat {
   @Shadow
   protected GuiTextField inputField;
   private boolean shouldDrawOutline;

   @Inject(
      method = {"keyTyped(CI)V"},
      at = {@At("RETURN")}
   )
   public void keyTypedHook(char typedChar, int keyCode, CallbackInfo info) {
      if (Wrapper.mc.currentScreen instanceof GuiChat) {
         this.shouldDrawOutline = this.inputField.getText().startsWith(Managers.COMMANDS.getCommandPrefix());
      } else {
         this.shouldDrawOutline = false;
      }
   }

   @Inject(
      method = {"drawScreen"},
      at = {@At("TAIL")}
   )
   public void drawScreenHook(int mouseX, int mouseY, float partialTicks, CallbackInfo info) {
      if (this.shouldDrawOutline) {
         boolean blend = GL11.glIsEnabled(3042);
         boolean texture2D = GL11.glIsEnabled(3553);
         GL11.glDisable(3042);
         GL11.glDisable(3553);
         RenderUtil.glColor(Managers.COLORS.getCurrent());
         GL11.glLineWidth(1.5F);
         GL11.glBegin(1);
         int x = this.inputField.x - 2;
         int y = this.inputField.y - 2;
         int width = this.inputField.width;
         int height = this.inputField.height;
         GL11.glVertex2d((double)x, (double)y);
         GL11.glVertex2d((double)(x + width), (double)y);
         GL11.glVertex2d((double)(x + width), (double)y);
         GL11.glVertex2d((double)(x + width), (double)(y + height));
         GL11.glVertex2d((double)(x + width), (double)(y + height));
         GL11.glVertex2d((double)x, (double)(y + height));
         GL11.glVertex2d((double)x, (double)(y + height));
         GL11.glVertex2d((double)x, (double)y);
         GL11.glEnd();
         if (blend) {
            GL11.glEnable(3042);
         }

         if (texture2D) {
            GL11.glEnable(3553);
         }
      }
   }
}
