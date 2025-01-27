//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "G:\PortableSoft\JBY\MC_Deobf3000\1.12-MCP-Mappings"!

package me.rebirthclient.api.util;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import me.rebirthclient.api.managers.Managers;
import me.rebirthclient.api.util.math.MathUtil;
import me.rebirthclient.mod.modules.impl.combat.CombatSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Mouse;

public class EntityUtil implements Wrapper {
   public static boolean isEating() {
      return mc.player.isHandActive() && mc.player.getActiveItemStack().getItem() instanceof ItemFood
         || mc.player.getHeldItemMainhand().getItem() instanceof ItemFood && Mouse.isButtonDown(1);
   }

   public static void faceYawAndPitch(float yaw, float pitch) {
      sendPlayerRot(yaw, pitch, mc.player.onGround);
   }

   public static boolean isInLiquid() {
      if (mc.player.fallDistance >= 3.0F) {
         return false;
      } else {
         boolean inLiquid = false;
         AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox() : mc.player.getEntityBoundingBox();
         int y = (int)bb.minY;

         for(int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; ++x) {
            for(int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; ++z) {
               Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
               if (!(block instanceof BlockAir)) {
                  if (!(block instanceof BlockLiquid)) {
                     return false;
                  }

                  inLiquid = true;
               }
            }
         }

         return inLiquid;
      }
   }

   public static void facePlacePos(BlockPos pos) {
      EnumFacing side = BlockUtil.getFirstFacing(pos);
      if (side != null) {
         BlockPos neighbour = pos.offset(side);
         EnumFacing opposite = side.getOpposite();
         Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
         faceVector(hitVec);
      }
   }

   public static void faceXYZ(double x, double y, double z) {
      faceYawAndPitch(getXYZYaw(x, y, z), getXYZPitch(x, y, z));
   }

   public static float getXYZYaw(double x, double y, double z) {
      float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(x, y, z));
      return angle[0];
   }

   public static float getXYZPitch(double x, double y, double z) {
      float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(x, y, z));
      return angle[1];
   }

   public static Vec3d getEyePosition(Entity entity) {
      return new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ);
   }

   public static Vec3d interpolateEntity(Entity entity, float time) {
      return new Vec3d(
         entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)time,
         entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)time,
         entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)time
      );
   }

   public static BlockPos getPlayerPos() {
      return getEntityPos(mc.player);
   }

   public static BlockPos getEntityPos(Entity target) {
      return new BlockPos(target.posX, target.posY + 0.5, target.posZ);
   }

   public static boolean invalid(Entity entity, double range) {
      return entity == null
         || isDead(entity)
         || entity.equals(mc.player)
         || entity instanceof EntityPlayer && Managers.FRIENDS.isFriend(entity.getName())
         || mc.player.getDistanceSq(entity) > MathUtil.square(range);
   }

   public static void sendPlayerRot(float yaw, float pitch, boolean onGround) {
      mc.player.connection.sendPacket(new Rotation(yaw, pitch, onGround));
   }

   public static float[] getLegitRotations(Vec3d vec) {
      Vec3d eyesPos = getEyesPos();
      double diffX = vec.x - eyesPos.x;
      double diffY = vec.y - eyesPos.y;
      double diffZ = vec.z - eyesPos.z;
      double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
      float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
      float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
      return new float[]{
         mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
         mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)
      };
   }

   public static Vec3d getEyesPos() {
      return new Vec3d(
         mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ
      );
   }

   public static void faceVector(Vec3d vec) {
      float[] rotations = getLegitRotations(vec);
      CombatSetting.vec = vec;
      CombatSetting.timer.reset();
      sendPlayerRot(rotations[0], rotations[1], mc.player.onGround);
   }

   public static void facePosFacing(BlockPos pos, EnumFacing side) {
      Vec3d hitVec = new Vec3d(pos).add(0.5, 0.5, 0.5).add(new Vec3d(side.getDirectionVec()).scale(0.5));
      faceVector(hitVec);
   }

   public static Vec3d[] getVarOffsets(int x, int y, int z) {
      List<Vec3d> offsets = getVarOffsetList(x, y, z);
      Vec3d[] array = new Vec3d[offsets.size()];
      return offsets.toArray(array);
   }

   public static List<Vec3d> getVarOffsetList(int x, int y, int z) {
      ArrayList<Vec3d> offsets = new ArrayList();
      offsets.add(new Vec3d((double)x, (double)y, (double)z));
      return offsets;
   }

   public static Vec3d getInterpolatedPos(Entity entity, float partialTicks) {
      return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, partialTicks));
   }

   public static Vec3d getInterpolatedRenderPos(Entity entity, float partialTicks) {
      return getInterpolatedPos(entity, partialTicks)
         .subtract(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);
   }

   public static Vec3d getInterpolatedAmount(Entity entity, Vec3d vec) {
      return getInterpolatedAmount(entity, vec.x, vec.y, vec.z);
   }

   public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
      return new Vec3d(
         (entity.posX - entity.lastTickPosX) * x,
         (entity.posY - entity.lastTickPosY) * y,
         (entity.posZ - entity.lastTickPosZ) * z
      );
   }

   public static Vec3d getInterpolatedAmount(Entity entity, float partialTicks) {
      return getInterpolatedAmount(entity, (double)partialTicks, (double)partialTicks, (double)partialTicks);
   }

   public static boolean isArmorLow(EntityPlayer player, int durability) {
      for(ItemStack piece : player.inventory.armorInventory) {
         if (piece == null) {
            return true;
         }

         if (getDamagePercent(piece) < durability) {
            return true;
         }
      }

      return false;
   }

   public static boolean canEntityBeSeen(Entity entityIn) {
      return mc.world
               .rayTraceBlocks(
                  new Vec3d(
                     mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ
                  ),
                  new Vec3d(entityIn.posX, entityIn.posY + (double)entityIn.getEyeHeight(), entityIn.posZ),
                  false,
                  true,
                  false
               )
            == null
         || mc.world
               .rayTraceBlocks(
                  new Vec3d(
                     mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ
                  ),
                  new Vec3d(entityIn.posX, entityIn.posY + 0.5, entityIn.posZ),
                  false,
                  true,
                  false
               )
            == null;
   }

   public static boolean isValid(Entity entity, double range) {
      boolean invalid = entity == null
         || isDead(entity)
         || entity.equals(mc.player)
         || entity instanceof EntityPlayer && Managers.FRIENDS.isFriend(entity.getName())
         || mc.player.getDistanceSq(entity) > MathUtil.square(range);
      return !invalid;
   }

   public static boolean isInHole(Entity entity) {
      return BlockUtil.isHole(new BlockPos(entity.posX, entity.posY, entity.posZ));
   }

   public static boolean isTrapped(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      return getUntrappedBlocks(player, antiScaffold, antiStep, legs, platform, antiDrop).size() == 0;
   }

   public static boolean isHoldingWeapon(EntityPlayer player) {
      return player.getHeldItemMainhand().getItem() instanceof ItemSword || player.getHeldItemMainhand().getItem() instanceof ItemAxe;
   }

   public static boolean isHolding32k(EntityPlayer player) {
      return EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, player.getHeldItemMainhand()) >= 1000;
   }

   public static boolean isSafe(Entity entity, int height, boolean floor) {
      return getUnsafeBlocksList(entity, height, floor).size() == 0;
   }

   public static boolean isSafe(Entity entity) {
      return isSafe(entity, 0, false);
   }

   public static boolean isPassive(Entity entity) {
      if (entity instanceof EntityWolf && ((EntityWolf)entity).isAngry()) {
         return false;
      } else if (!(entity instanceof EntityAgeable) && !(entity instanceof EntityAmbientCreature) && !(entity instanceof EntitySquid)) {
         return entity instanceof EntityIronGolem && ((EntityIronGolem)entity).getRevengeTarget() == null;
      } else {
         return true;
      }
   }

   public static boolean isMobAggressive(Entity entity) {
      if (entity instanceof EntityPigZombie) {
         if (((EntityPigZombie)entity).isArmsRaised() || ((EntityPigZombie)entity).isAngry()) {
            return true;
         }
      } else {
         if (entity instanceof EntityWolf) {
            return ((EntityWolf)entity).isAngry() && !mc.player.equals(((EntityWolf)entity).getOwner());
         }

         if (entity instanceof EntityEnderman) {
            return ((EntityEnderman)entity).isScreaming();
         }
      }

      return isHostileMob(entity);
   }

   public static boolean isNeutralMob(Entity entity) {
      return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
   }

   public static boolean isProjectile(Entity entity) {
      return entity instanceof EntityShulkerBullet || entity instanceof EntityFireball;
   }

   public static boolean isVehicle(Entity entity) {
      return entity instanceof EntityBoat || entity instanceof EntityMinecart;
   }

   public static boolean isHostileMob(Entity entity) {
      return entity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(entity);
   }

   public static boolean isLiving(Entity entity) {
      return entity instanceof EntityLivingBase;
   }

   public static boolean isAlive(Entity entity) {
      return isLiving(entity) && !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0.0F;
   }

   public static boolean isDead(Entity entity) {
      return !isAlive(entity);
   }

   public static EntityPlayer getCopiedPlayer(EntityPlayer player) {
      final int count = player.getItemInUseCount();
      EntityPlayer copied = new EntityPlayer(mc.world, new GameProfile(UUID.randomUUID(), player.getName())) {
         public boolean isSpectator() {
            return false;
         }

         public boolean isCreative() {
            return false;
         }

         public int getItemInUseCount() {
            return count;
         }
      };
      copied.setSneaking(player.isSneaking());
      copied.swingProgress = player.swingProgress;
      copied.limbSwing = player.limbSwing;
      copied.limbSwingAmount = player.prevLimbSwingAmount;
      copied.inventory.copyInventory(player.inventory);
      copied.setPrimaryHand(player.getPrimaryHand());
      copied.ticksExisted = player.ticksExisted;
      copied.setEntityId(player.getEntityId());
      copied.copyLocationAndAnglesFrom(player);
      return copied;
   }

   public static int getHitCoolDown(EntityPlayer player) {
      Item item = player.getHeldItemMainhand().getItem();
      if (item instanceof ItemSword) {
         return 600;
      } else if (item instanceof ItemPickaxe) {
         return 850;
      } else if (item == Items.IRON_AXE) {
         return 1100;
      } else if (item == Items.STONE_HOE) {
         return 500;
      } else if (item == Items.IRON_HOE) {
         return 350;
      } else if (item == Items.WOODEN_AXE || item == Items.STONE_AXE) {
         return 1250;
      } else {
         return !(item instanceof ItemSpade)
               && item != Items.GOLDEN_AXE
               && item != Items.DIAMOND_AXE
               && item != Items.WOODEN_HOE
               && item != Items.GOLDEN_HOE
            ? 250
            : 1000;
      }
   }

   public static EntityPlayer getClosestEnemy(double distance) {
      EntityPlayer closest = null;

      for(EntityPlayer player : mc.world.playerEntities) {
         if (isValid(player, distance)) {
            if (closest == null) {
               closest = player;
            } else if (mc.player.getDistanceSq(player) < mc.player.getDistanceSq(closest)) {
               closest = player;
            }
         }
      }

      return closest;
   }

   public static List<Vec3d> getUntrappedBlocks(EntityPlayer player, boolean extraTop, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      ArrayList<Vec3d> vec3ds = new ArrayList();
      if (!antiStep && getUnsafeBlocksList(player, 2, false).size() == 4) {
         vec3ds.addAll(getUnsafeBlocksList(player, 2, false));
      }

      for(int i = 0; i < getTrapOffsets(extraTop, antiStep, legs, platform, antiDrop).length; ++i) {
         Vec3d vector = getTrapOffsets(extraTop, antiStep, legs, platform, antiDrop)[i];
         BlockPos targetPos = new BlockPos(player.getPositionVector()).add(vector.x, vector.y, vector.z);
         Block block = mc.world.getBlockState(targetPos).getBlock();
         if (block instanceof BlockAir
            || block instanceof BlockLiquid
            || block instanceof BlockTallGrass
            || block instanceof BlockFire
            || block instanceof BlockDeadBush
            || block instanceof BlockSnow) {
            vec3ds.add(vector);
         }
      }

      return vec3ds;
   }

   public static List<Vec3d> getTrapOffsetList(
      Vec3d vec, boolean extraTop, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace
   ) {
      ArrayList<Vec3d> retval = new ArrayList();
      if (!antiStep) {
         List<Vec3d> offset = getUnsafeBlocksList(vec, 2, false);
         if (offset.size() == 4) {
            for(Vec3d vector : offset) {
               BlockPos pos = new BlockPos(vec).add(vector.x, vector.y, vector.z);
               switch(BlockUtil.getPlaceAbility(pos, raytrace)) {
                  case -1:
                  case 1:
                  case 2:
                     break;
                  case 3:
                     retval.add(vec.add(vector));
                  case 0:
                  default:
                     Collections.addAll(retval, MathUtil.convertVectors(vec, getTrapOffsets(extraTop, false, legs, platform, antiDrop)));
                     return retval;
               }
            }
         }
      }

      Collections.addAll(retval, MathUtil.convertVectors(vec, getTrapOffsets(extraTop, antiStep, legs, platform, antiDrop)));
      return retval;
   }

   public static Vec3d[] getTrapOffsets(boolean extraTop, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      ArrayList<Vec3d> offsetArray = new ArrayList(getOffsetList(1, false));
      offsetArray.add(new Vec3d(0.0, 2.0, 0.0));
      if (extraTop) {
         offsetArray.add(new Vec3d(0.0, 3.0, 0.0));
      }

      if (antiStep) {
         offsetArray.addAll(getOffsetList(2, false));
      }

      if (legs) {
         offsetArray.addAll(getOffsetList(0, false));
      }

      if (platform) {
         offsetArray.addAll(getOffsetList(-1, false));
         offsetArray.add(new Vec3d(0.0, -1.0, 0.0));
      }

      if (antiDrop) {
         offsetArray.add(new Vec3d(0.0, -2.0, 0.0));
      }

      Vec3d[] array = new Vec3d[offsetArray.size()];
      return offsetArray.toArray(array);
   }

   public static List<Vec3d> getOffsetList(int y, boolean floor) {
      ArrayList<Vec3d> offsets = new ArrayList();
      offsets.add(new Vec3d(-1.0, (double)y, 0.0));
      offsets.add(new Vec3d(1.0, (double)y, 0.0));
      offsets.add(new Vec3d(0.0, (double)y, -1.0));
      offsets.add(new Vec3d(0.0, (double)y, 1.0));
      if (floor) {
         offsets.add(new Vec3d(0.0, (double)(y - 1), 0.0));
      }

      return offsets;
   }

   public static Vec3d[] getOffsets(int y, boolean floor) {
      List<Vec3d> offsets = getOffsetList(y, floor);
      Vec3d[] array = new Vec3d[offsets.size()];
      return offsets.toArray(array);
   }

   public static List<Vec3d> getUnsafeBlocksList(Vec3d pos, int height, boolean floor) {
      ArrayList<Vec3d> vec3ds = new ArrayList();

      for(Vec3d vector : getOffsets(height, floor)) {
         BlockPos targetPos = new BlockPos(pos).add(vector.x, vector.y, vector.z);
         Block block = mc.world.getBlockState(targetPos).getBlock();
         if (block instanceof BlockAir
            || block instanceof BlockLiquid
            || block instanceof BlockTallGrass
            || block instanceof BlockFire
            || block instanceof BlockDeadBush
            || block instanceof BlockSnow) {
            vec3ds.add(vector);
         }
      }

      return vec3ds;
   }

   public static List<Vec3d> getUnsafeBlocksList(Entity entity, int height, boolean floor) {
      return getUnsafeBlocksList(entity.getPositionVector(), height, floor);
   }

   public static float getHealth(Entity entity) {
      if (isLiving(entity)) {
         EntityLivingBase livingBase = (EntityLivingBase)entity;
         return livingBase.getHealth() + livingBase.getAbsorptionAmount();
      } else {
         return 0.0F;
      }
   }

   public static BlockPos getRoundedPos(Entity entity) {
      return new BlockPos(MathUtil.roundVec(entity.getPositionVector(), 0));
   }

   public static int getDamagePercent(ItemStack stack) {
      return (int)((double)(stack.getMaxDamage() - stack.getItemDamage()) / Math.max(0.1, (double)stack.getMaxDamage()) * 100.0);
   }

   public static boolean stopSneaking(boolean isSneaking) {
      if (isSneaking && mc.player != null) {
         mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
      }

      return false;
   }
}
