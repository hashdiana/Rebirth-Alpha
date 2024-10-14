package me.rebirthclient.mod.modules.impl.combat;

import me.rebirthclient.api.events.impl.MotionEvent;
import me.rebirthclient.api.util.EntityUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Category;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.settings.Setting;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CombatSetting extends Module {
   public static CombatSetting INSTANCE;
   public final Setting<Boolean> strictPlace = this.add(new Setting<>("StrictPlace", false).setParent());
   public final Setting<Boolean> checkRaytrace = this.add(new Setting<>("CheckRaytrace", false, v -> this.strictPlace.isOpen()));
   public final Setting<Boolean> resetRotation = this.add(new Setting<>("ResetRotation", false));
   public final Setting<Boolean> resetPosition = this.add(new Setting<>("ResetPosition", false));
   public final Setting<Integer> attackDelay = this.add(new Setting<>("AttackDelay", 300, 0, 1000));
   private final Setting<Integer> rotateTimer = this.add(new Setting<>("RotateTime", 300, 0, 1000));
   public final Setting<Boolean> placeInFire = this.add(new Setting<>("PlaceInFire", true));
   public static final Timer timer = new Timer();
   public static Vec3d vec;

   public CombatSetting() {
      super("CombatSetting", "idk", Category.COMBAT);
      INSTANCE = this;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public final void onMotion(MotionEvent event) {
      if (!fullNullCheck()) {
         if (!timer.passedMs((long)this.rotateTimer.getValue().intValue()) && vec != null) {
            this.faceVector(vec, event);
         }
      }
   }

   private void faceVector(Vec3d vec, MotionEvent event) {
      float[] rotations = EntityUtil.getLegitRotations(vec);
      event.setRotation(rotations[0], rotations[1]);
   }
}
