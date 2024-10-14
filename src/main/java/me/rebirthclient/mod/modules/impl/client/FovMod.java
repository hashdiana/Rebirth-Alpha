//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.rebirthclient.mod.modules.impl.client;

import me.rebirthclient.api.events.impl.PerspectiveEvent;
import me.rebirthclient.mod.modules.Category;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.settings.Setting;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FovMod extends Module {
   public static FovMod INSTANCE = new FovMod();
   private final Setting<FovMod.Page> page = this.add(new Setting<>("Settings", FovMod.Page.FOV));
   private final Setting<Boolean> customFov = this.add(new Setting<>("CustomFov", false, v -> this.page.getValue() == FovMod.Page.FOV).setParent());
   private final Setting<Float> fov = this.add(
      new Setting<>("FOV", 120.0F, 10.0F, 180.0F, v -> this.page.getValue() == FovMod.Page.FOV && this.customFov.isOpen())
   );
   private final Setting<Boolean> aspectRatio = this.add(new Setting<>("AspectRatio", false, v -> this.page.getValue() == FovMod.Page.FOV).setParent());
   private final Setting<Float> aspectFactor = this.add(
      new Setting<>("AspectFactor", 1.8F, 0.1F, 3.0F, v -> this.page.getValue() == FovMod.Page.FOV && this.aspectRatio.isOpen())
   );
   private final Setting<Boolean> defaults = this.add(new Setting<>("Defaults", false, v -> this.page.getValue() == FovMod.Page.ADVANCED));
   private final Setting<Float> sprint = this.add(new Setting<>("SprintAdd", 1.15F, 1.0F, 2.0F, v -> this.page.getValue() == FovMod.Page.ADVANCED));
   private final Setting<Float> speed = this.add(new Setting<>("SwiftnessAdd", 1.15F, 1.0F, 2.0F, v -> this.page.getValue() == FovMod.Page.ADVANCED));

   public FovMod() {
      super("FovMod", "FOV modifier", Category.CLIENT);
      INSTANCE = this;
   }

   @Override
   public void onUpdate() {
      if (this.customFov.getValue()) {
         mc.gameSettings.setOptionFloatValue(Options.FOV, this.fov.getValue());
      }

      if (this.defaults.getValue()) {
         this.sprint.setValue(1.15F);
         this.speed.setValue(1.15F);
         this.defaults.setValue(false);
      }
   }

   @SubscribeEvent
   public void onFOVUpdate(FOVUpdateEvent event) {
      if (!fullNullCheck()) {
         float fov = 1.0F;
         if (event.getEntity().isSprinting()) {
            fov = this.sprint.getValue();
            if (event.getEntity().isPotionActive(MobEffects.SPEED)) {
               fov = this.speed.getValue();
            }
         }

         event.setNewfov(fov);
      }
   }

   @SubscribeEvent
   public void onPerspectiveUpdate(PerspectiveEvent event) {
      if (!fullNullCheck()) {
         if (this.aspectRatio.getValue()) {
            event.setAngle(this.aspectFactor.getValue());
         }
      }
   }

   public static enum Page {
      FOV,
      ADVANCED;
   }
}
