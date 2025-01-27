package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SPacketPlayerPosLook implements Packet<INetHandlerPlayClient>
{
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public Set<SPacketPlayerPosLook.EnumFlags> flags;
    public int teleportId;

    public SPacketPlayerPosLook()
    {
    }

    public SPacketPlayerPosLook(double xIn, double yIn, double zIn, float yawIn, float pitchIn, Set<SPacketPlayerPosLook.EnumFlags> flagsIn, int teleportIdIn)
    {
        this.x = xIn;
        this.y = yIn;
        this.z = zIn;
        this.yaw = yawIn;
        this.pitch = pitchIn;
        this.flags = flagsIn;
        this.teleportId = teleportIdIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
        this.flags = SPacketPlayerPosLook.EnumFlags.unpack(buf.readUnsignedByte());
        this.teleportId = buf.readVarInt();
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
        buf.writeByte(SPacketPlayerPosLook.EnumFlags.pack(this.flags));
        buf.writeVarInt(this.teleportId);
    }

    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handlePlayerPosLook(this);
    }

    @SideOnly(Side.CLIENT)
    public double getX()
    {
        return this.x;
    }

    @SideOnly(Side.CLIENT)
    public double getY()
    {
        return this.y;
    }

    @SideOnly(Side.CLIENT)
    public double getZ()
    {
        return this.z;
    }

    @SideOnly(Side.CLIENT)
    public float getYaw()
    {
        return this.yaw;
    }

    @SideOnly(Side.CLIENT)
    public float getPitch()
    {
        return this.pitch;
    }

    @SideOnly(Side.CLIENT)
    public int getTeleportId()
    {
        return this.teleportId;
    }

    @SideOnly(Side.CLIENT)
    public Set<SPacketPlayerPosLook.EnumFlags> getFlags()
    {
        return this.flags;
    }

    public static enum EnumFlags
    {
        X(0),
        Y(1),
        Z(2),
        Y_ROT(3),
        X_ROT(4);

        private final int bit;

        private EnumFlags(int bitIn)
        {
            this.bit = bitIn;
        }

        private int getMask()
        {
            return 1 << this.bit;
        }

        private boolean isSet(int flags)
        {
            return (flags & this.getMask()) == this.getMask();
        }

        public static Set<SPacketPlayerPosLook.EnumFlags> unpack(int flags)
        {
            Set<SPacketPlayerPosLook.EnumFlags> set = EnumSet.<SPacketPlayerPosLook.EnumFlags>noneOf(SPacketPlayerPosLook.EnumFlags.class);

            for (SPacketPlayerPosLook.EnumFlags spacketplayerposlook$enumflags : values())
            {
                if (spacketplayerposlook$enumflags.isSet(flags))
                {
                    set.add(spacketplayerposlook$enumflags);
                }
            }

            return set;
        }

        public static int pack(Set<SPacketPlayerPosLook.EnumFlags> flags)
        {
            int i = 0;

            for (SPacketPlayerPosLook.EnumFlags spacketplayerposlook$enumflags : flags)
            {
                i |= spacketplayerposlook$enumflags.getMask();
            }

            return i;
        }
    }
}