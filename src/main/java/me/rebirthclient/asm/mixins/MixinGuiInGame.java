package me.rebirthclient.asm.mixins;

import me.rebirthclient.mod.modules.impl.client.HUD;
import me.rebirthclient.mod.modules.impl.render.NoLag;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiIngame.class})
public abstract class MixinGuiInGame extends Gui {
   @Inject(
      method = {"renderPotionEffects"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void renderPotionEffectsHook(ScaledResolution scaledRes, CallbackInfo info) {
      HUD mod = HUD.INSTANCE;
      if (mod.potionIcons.getValue() && mod.isOn()) {
         info.cancel();
      }
   }

   @Inject(
      method = {"renderScoreboard"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void renderScoreboardHook(ScoreObjective objective, ScaledResolution scaledRes, CallbackInfo info) {
      NoLag mod = NoLag.INSTANCE;
      if (mod.scoreBoards.getValue() && mod.isOn()) {
         info.cancel();
      }
   }
}
