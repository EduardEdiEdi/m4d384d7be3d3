package better.anticheat.core.util.math;

import lombok.experimental.UtilityClass;

@UtilityClass
public class VanillaMath {

    public static final float PI = (float) Math.PI;
    public static final float HALF_PI = (float) (Math.PI / 2);
    public static final float TWO_PI = (float) (Math.PI * 2);
    public static final float DEG_TO_RAD = (float) (Math.PI / 180.0);
    public static final float RAD_TO_DEG = 180.0F / (float)Math.PI;

    private static final float[] SIN;

    static {
        SIN = new float[65536];
        for (int i = 0; i < SIN.length; i++) {
            SIN[i] = (float) Math.sin((double) i * Math.PI * 2.0 / 65536.0);
        }
    }

    public static float degreesToRadians(float degrees) {
        return degrees * DEG_TO_RAD;
    }

    public static float sin(float radians) {
        return SIN[(int)(radians * 10430.378F) & 65535];
    }

    public static float cos(float radians) {
        return SIN[(int)(radians * 10430.378F + 16384.0F) & 65535];
    }
}
