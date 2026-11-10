package better.anticheat.core.util.entity;

import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EntityMath {
    public static double[] getOffsetFromLocation(Vector3d one, Vector3d two, final float oneYaw, final float onePitch) {
        double yaw = getRotations(one, two)[0];
        double pitch = getRotations(one, two)[1];
        double yawOffset = Math.abs(yaw - yawTo180F(oneYaw));
        double pitchOffset = Math.abs(pitch - onePitch);
        return new double[]{yawTo180F((float) yawOffset), pitchOffset};
    }

    public static double[] getOffsetFromLocation(Location one, Location two) {
        return getOffsetFromLocation(one.getPosition(), two.getPosition(), one.getYaw(), two.getPitch());
    }

    public static float[] getRotations(Location one, Location two) {
        return getRotations(one.getPosition(), two.getPosition());
    }

    public static float[] getRotations(Vector3d one, Vector3d two) {
        if (one == null || two == null) return new float[]{0, 0};

        double diffX = two.getX() - one.getX();
        double diffZ = two.getZ() - one.getZ();
        double diffY = two.getY() + 2.0 - 0.4 - (one.getY() + 2.0);
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }

    public static float yawTo180F(float flub) {
        if ((flub %= 360.0f) >= 180.0f) {
            flub -= 360.0f;
        }
        if (flub < -180.0f) {
            flub += 360.0f;
        }
        return flub;
    }
}
