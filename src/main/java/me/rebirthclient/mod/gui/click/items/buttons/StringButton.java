//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.rebirthclient.mod.gui.click.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.rebirthclient.api.managers.Managers;
import me.rebirthclient.api.util.render.RenderUtil;
import me.rebirthclient.mod.gui.screen.Gui;
import me.rebirthclient.mod.modules.impl.client.ClickGui;
import me.rebirthclient.mod.modules.settings.Setting;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatAllowedCharacters;

public class StringButton extends Button {
   private final Setting setting;
   public boolean isListening;
   private StringButton.CurrentString currentString = new StringButton.CurrentString("");

   public StringButton(Setting setting) {
      super(setting.getName());
      this.setting = setting;
      this.setString(setting.getValue().toString());
      this.width = 15;
   }

   public static String removeLastChar(String str) {
      String output = "";
      if (str != null && str.length() > 0) {
         output = str.substring(0, str.length() - 1);
      }

      return output;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      boolean future = ClickGui.INSTANCE.style.getValue() == ClickGui.Style.FUTURE;
      boolean dotgod = ClickGui.INSTANCE.style.getValue() == ClickGui.Style.DOTGOD;
      if (future) {
         RenderUtil.drawRect(
            this.x,
            this.y,
            this.x + (float)this.width + 7.4F,
            this.y + (float)this.height - 0.5F,
            !this.isHovering(mouseX, mouseY) ? Managers.COLORS.getCurrentWithAlpha(26) : Managers.COLORS.getCurrentWithAlpha(90)
         );
      } else if (dotgod) {
         RenderUtil.drawRect(
            this.x,
            this.y,
            this.x + (float)this.width + 7.4F,
            this.y + (float)this.height - 0.5F,
            !this.isHovering(mouseX, mouseY) ? Managers.COLORS.getCurrentWithAlpha(26) : Managers.COLORS.getCurrentWithAlpha(120)
         );
      }

      if (this.isListening) {
         Managers.TEXT
            .drawStringWithShadow(
               this.currentString.getString() + Managers.TEXT.getIdleSign(),
               this.x + 2.3F,
               this.y - 1.7F - (float)Gui.INSTANCE.getTextOffset(),
               this.getState() ? -1 : -5592406
            );
      } else {
         Managers.TEXT
            .drawStringWithShadow(
               this.setting.getName() + " " + ChatFormatting.GRAY + this.setting.getValue(),
               this.x + 2.3F,
               this.y - 1.7F - (float)Gui.INSTANCE.getTextOffset(),
               this.getState() ? -1 : -5592406
            );
      }
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (this.isHovering(mouseX, mouseY)) {
         mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      }
   }

   @Override
   public void onKeyTyped(char typedChar, int keyCode) {
      if (this.isListening) {
         switch(keyCode) {
            case 1:
               return;
            case 28:
               this.enterString();
            case 14:
               this.setString(removeLastChar(this.currentString.getString()));
            default:
               if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                  this.setString(this.currentString.getString() + typedChar);
               }
         }
      }
   }

   @Override
   public void update() {
      this.setHidden(!this.setting.isVisible());
   }

   private void enterString() {
      if (this.currentString.getString().isEmpty()) {
         this.setting.setValue(this.setting.getDefaultValue());
      } else {
         this.setting.setValue(this.currentString.getString());
      }

      this.setString(this.setting.getValue().toString() + "1");
      this.onMouseClick();
   }

   @Override
   public int getHeight() {
      return ClickGui.INSTANCE.getButtonHeight() - 1;
   }

   @Override
   public void toggle() {
      this.isListening = !this.isListening;
   }

   @Override
   public boolean getState() {
      return !this.isListening;
   }

   public void setString(String newString) {
      this.currentString = new StringButton.CurrentString(newString);
   }

   public static class CurrentString {
      private final String string;

      public CurrentString(String string) {
         this.string = string;
      }

      public String getString() {
         return this.string;
      }
   }
}
