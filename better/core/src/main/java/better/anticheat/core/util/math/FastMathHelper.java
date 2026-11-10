package better.anticheat.core.util.math;

/**
 * Fast inaccurate math
 */
public class FastMathHelper {

    public static final float SQRT_2 = sqrt_float(2.0F);
    public static final float PI = roundToFloat(Math.PI);
    public static final float PI2 = roundToFloat((Math.PI * 2D));
    public static final float PId2 = roundToFloat((Math.PI / 2D));
    public static final float deg2Rad = roundToFloat(0.017453292519943295D);
    private static final int SIN_BITS = 12;
    private static final int SIN_MASK = 4095;
    private static final int SIN_COUNT = 4096;
    private static final int SIN_COUNT_D4 = 1024;
    private static final float radToIndex = roundToFloat(651.8986469044033D);
    private static final float[] SIN_TABLE_FAST = new float[4096];

    /**
     * Though it looks like an array, this is really more like a mapping.  Key (index of this array) is the upper 5 bits
     * of the result of multiplying a 32-bit unsigned integer by the B(2, 5) De Bruijn sequence 0x077CB531.  Value
     * (value stored in the array) is the unique index (from the right) of the leftmost one-bit in a 32-bit unsigned
     * integer that can cause the upper 5 bits to get that value.  Used for highly optimized "find the log-base-2 of
     * this number" calculations.
     */
    private static final int[] multiplyDeBruijnBitPosition;

    static {
        for (int j = 0; j < SIN_TABLE_FAST.length; ++j) {
            SIN_TABLE_FAST[j] = roundToFloat(Math.sin((double) j * Math.PI * 2.0D / 4096.0D));
        }

        multiplyDeBruijnBitPosition = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    }

    public static float roundToFloat(double d) {
        return (float) ((double) Math.round(d * 1.0E8D) / 1.0E8D);
    }

    /**
     * sin looked up in a table
     */
    public static float sin(float p_76126_0_) {
        return SIN_TABLE_FAST[(int) (p_76126_0_ * radToIndex) & 4095];
    }

    /**
     * cos looked up in the sin table with the appropriate offset
     */
    public static float cos(float value) {
        return SIN_TABLE_FAST[(int) (value * radToIndex + 1024.0F) & 4095];
    }

    public static float sqrt_float(float value) {
        return (float) Math.sqrt(value);
    }

    /**
     * Returns the input value rounded up to the next highest power of two.
     */
    public static int roundUpToPowerOfTwo(int value) {
        int i = value - 1;
        i = i | i >> 1;
        i = i | i >> 2;
        i = i | i >> 4;
        i = i | i >> 8;
        i = i | i >> 16;
        return i + 1;
    }

    /**
     * Is the given value a power of two?  (1, 2, 4, 8, 16, ...)
     */
    public static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    /**
     * Uses a B(2, 5) De Bruijn sequence and a lookup table to efficiently calculate the log-base-two of the given
     * value.  Optimized for cases where the input value is a power-of-two.  If the input value is not a power-of-two,
     * then subtract 1 from the return value.
     */
    public static int calculateLogBaseTwo(int value) {
        value = isPowerOfTwo(value) ? value : roundUpToPowerOfTwo(value);
        return multiplyDeBruijnBitPosition[(int) ((long) value * 125613361L >> 27) & 31];
    }
}