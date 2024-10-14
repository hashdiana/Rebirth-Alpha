//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.rebirthclient.mod.modules.impl.movement;

import java.util.Objects;
import me.rebirthclient.api.events.impl.MoveEvent;
import me.rebirthclient.api.events.impl.PacketEvent;
import me.rebirthclient.api.events.impl.PushEvent;
import me.rebirthclient.api.events.impl.UpdateWalkingPlayerEvent;
import me.rebirthclient.api.util.MovementUtil;
import me.rebirthclient.api.util.PositionUtil;
import me.rebirthclient.api.util.Timer;
import me.rebirthclient.mod.modules.Category;
import me.rebirthclient.mod.modules.Module;
import me.rebirthclient.mod.modules.settings.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class OldSpeed extends Module {
   public static OldSpeed INSTANCE = new OldSpeed();
   private final Setting<Boolean> jump = this.add(new Setting<>("Jump", false));
   private final Setting<Boolean> inWater = this.add(new Setting<>("InWater", false));
   private final Setting<Double> strafeSpeed = this.add(new Setting<>("StrafeSpeed", 278.5, 100.0, 1000.0));
   private final Setting<Boolean> explosions = this.add(new Setting<>("Explosions", true));
   private final Setting<Boolean> velocity = this.add(new Setting<>("Velocity", true));
   private final Setting<Float> multiplier = this.add(new Setting<>("H-Factor", 1.0F, 0.0F, 5.0F));
   private final Setting<Float> vertical = this.add(new Setting<>("V-Factor", 1.0F, 0.0F, 5.0F));
   private final Setting<Integer> coolDown = this.add(new Setting<>("CoolDown", 400, 0, 5000));
   private final Setting<Integer> pauseTime = this.add(new Setting<>("PauseTime", 400, 0, 1000));
   private final Setting<Boolean> directional = this.add(new Setting<>("Directional", false));
   private final Setting<Double> cap = this.add(new Setting<>("Cap", 10.0, 0.0, 10.0));
   private final Setting<Boolean> scaleCap = this.add(new Setting<>("ScaleCap", false));
   private final Setting<Boolean> slow = this.add(new Setting<>("Slowness", true));
   private final Setting<Boolean> modify = this.add(new Setting<>("Modify", false));
   private final Setting<Double> xzFactor = this.add(new Setting<>("XZ-Factor", 1.0, 0.0, 5.0, v -> this.modify.getValue()));
   private final Setting<Double> yFactor = this.add(new Setting<>("Y-Factor", 1.0, 0.0, 5.0, v -> this.modify.getValue()));
   private final Setting<Boolean> debug = this.add(new Setting<>("Debug", false));
   private final Timer expTimer = new Timer();
   private boolean stop;
   private double speed;
   private double distance;
   private int stage;
   private double lastExp;
   private boolean boost;

   public OldSpeed() {
      super("OldSpeed", "3ar", Category.MOVEMENT);
      INSTANCE = this;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onReceivePacket(PacketEvent.Receive event) {
      if (!fullNullCheck()) {
         if (event.getPacket() instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity packet = event.getPacket();
            if (packet.getEntityID() == mc.player.getEntityId() && !this.directional.getValue() && this.velocity.getValue()) {
               double speed = Math.sqrt((double)(packet.getMotionX() * packet.getMotionX() + packet.getMotionZ() * packet.getMotionZ())) / 8000.0;
               this.lastExp = this.expTimer.passedMs((long)this.coolDown.getValue().intValue()) ? speed : speed - this.lastExp;
               if (this.lastExp > 0.0) {
                  if (this.debug.getValue()) {
                     this.sendMessage("boost");
                  }

                  this.expTimer.reset();
                  this.speed += this.lastExp * (double)this.multiplier.getValue().floatValue();
                  this.distance += this.lastExp * (double)this.multiplier.getValue().floatValue();
                  if (mc.player.motionY > 0.0 && this.vertical.getValue() != 0.0F) {
                     mc.player.motionY *= (double)this.vertical.getValue().floatValue();
                  }
               }
            }
         } else if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.distance = 0.0;
            this.speed = 0.0;
            this.stage = 4;
         } else if (event.getPacket() instanceof SPacketExplosion && this.explosions.getValue() && MovementUtil.isMoving()) {
            SPacketExplosion packet = event.getPacket();
            BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
            if (mc.player.getDistanceSq(pos) < 100.0
               && (!this.directional.getValue() || !MovementUtil.isInMovementDirection(packet.getX(), packet.getY(), packet.getZ()))
               )
             {
               double speed = Math.sqrt((double)(packet.getMotionX() * packet.getMotionX() + packet.getMotionZ() * packet.getMotionZ()));
               this.lastExp = this.expTimer.passedMs((long)this.coolDown.getValue().intValue()) ? speed : speed - this.lastExp;
               if (this.lastExp > 0.0) {
                  if (this.debug.getValue()) {
                     this.sendMessage("boost");
                  }

                  this.expTimer.reset();
                  this.speed += this.lastExp * (double)this.multiplier.getValue().floatValue();
                  this.distance += this.lastExp * (double)this.multiplier.getValue().floatValue();
                  if (mc.player.motionY > 0.0) {
                     mc.player.motionY *= (double)this.vertical.getValue().floatValue();
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void onPush(PushEvent event) {
      if (event.getStage() == 0 && event.entity.equals(mc.player)) {
         event.x = -event.x * 0.0;
         event.y = -event.y * 0.0;
         event.z = -event.z * 0.0;
      } else if (event.getStage() == 1) {
         event.setCanceled(true);
      } else if (event.getStage() == 2 && mc.player != null && mc.player.equals(event.entity)) {
         event.setCanceled(true);
      }
   }

   @Override
   public String getInfo() {
      return "3arthh4ck";
   }

   @Override
   public void onEnable() {
      this.speed = MovementUtil.getSpeed();
      this.distance = MovementUtil.getDistance2D();
      this.stage = 4;
   }

   private boolean isFlying(EntityPlayer player) {
      return player.isElytraFlying() || player.capabilities.isFlying || Flight.INSTANCE.isOn();
   }

   @SubscribeEvent
   public void Update(UpdateWalkingPlayerEvent event) {
      if (this.expTimer.passedMs((long)this.pauseTime.getValue().intValue())) {
         this.distance = MovementUtil.getDistance2D();
      }
   }

   @SubscribeEvent
   public void Move(MoveEvent event) {
      if (!fullNullCheck()) {
         if (!this.isFlying(mc.player)) {
            if ((this.inWater.getValue() || !PositionUtil.inLiquid() && !PositionUtil.inLiquid(true))
               && !mc.player.isOnLadder()
               && !mc.player.isEntityInsideOpaqueBlock()) {
               if (this.stop) {
                  this.stop = false;
               } else {
                  if (!MovementUtil.isMoving()) {
                     mc.player.motionX = 0.0;
                     mc.player.motionZ = 0.0;
                  }

                  this.playerMove(event);
                  if (this.modify.getValue()) {
                     event.setX(event.getX() * this.xzFactor.getValue());
                     event.setY(event.getY() * this.yFactor.getValue());
                     event.setZ(event.getZ() * this.xzFactor.getValue());
                  }
               }
            } else {
               this.stop = true;
            }
         }
      }
   }

   public double getCap() {
      double ret = this.cap.getValue();
      if (!this.scaleCap.getValue()) {
         return ret;
      } else {
         if (mc.player.isPotionActive(MobEffects.SPEED)) {
            int amplifier = ((PotionEffect)Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED))).getAmplifier();
            ret *= 1.0 + 0.2 * (double)(amplifier + 1);
         }

         if (this.slow.getValue() && mc.player.isPotionActive(MobEffects.SLOWNESS)) {
            int amplifier = ((PotionEffect)Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SLOWNESS))).getAmplifier();
            ret /= 1.0 + 0.2 * (double)(amplifier + 1);
         }

         return ret;
      }
   }

   public void playerMove(MoveEvent event) {
      if (MovementUtil.isMoving()) {
         if (!LongJump.INSTANCE.isOn()) {
            if (!Flight.INSTANCE.isOn()) {
               if (this.stage == 1) {
                  this.speed = 1.35 * MovementUtil.getSpeed(this.slow.getValue(), this.strafeSpeed.getValue() / 1000.0) - 0.01;
               } else if (this.stage == 2) {
                  if (this.jump.getValue()
                     || mc.gameSettings.keyBindJump.isKeyDown()
                     || InventoryMove.INSTANCE.isOn() && Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                     double yMotion = 0.3999 + MovementUtil.getJumpSpeed();
                     mc.player.motionY = yMotion;
                     event.setY(yMotion);
                     this.speed *= this.boost ? 1.6835 : 1.395;
                  }
               } else if (this.stage == 3) {
                  this.speed = this.distance - 0.66 * (this.distance - MovementUtil.getSpeed(this.slow.getValue(), this.strafeSpeed.getValue() / 1000.0));
                  this.boost = !this.boost;
               } else {
                  if ((
                        mc.world.getCollisionBoxes(null, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size()
                              > 0
                           || mc.player.collidedVertically
                     )
                     && this.stage > 0) {
                     this.stage = MovementUtil.isMoving() ? 1 : 0;
                  }

                  this.speed = this.distance - this.distance / 159.0;
               }

               this.speed = Math.min(this.speed, this.getCap());
               this.speed = Math.max(this.speed, MovementUtil.getSpeed(this.slow.getValue(), this.strafeSpeed.getValue() / 1000.0));
               MovementUtil.strafe(event, this.speed);
               ++this.stage;
            }
         }
      }
   }
}
