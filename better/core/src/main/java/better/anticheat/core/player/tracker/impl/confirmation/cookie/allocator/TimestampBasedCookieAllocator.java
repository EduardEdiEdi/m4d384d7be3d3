package better.anticheat.core.player.tracker.impl.confirmation.cookie.allocator;

import better.anticheat.core.player.tracker.impl.confirmation.cookie.CookieIdAllocator;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

/**
 * A cookie ID allocator that combines timestamp and random data.
 * This provides both uniqueness (via timestamp) and unpredictability (via random bytes).
 * The format is: [8 bytes timestamp][N bytes random data]
 */
public class TimestampBasedCookieAllocator implements CookieIdAllocator {
    
    private final SecureRandom random;
    private final int randomBytesLength;
    private final int totalLength;
    
    public TimestampBasedCookieAllocator() {
        this(4); // Default 4 random bytes + 8 timestamp bytes = 12 total
    }
    
    public TimestampBasedCookieAllocator(int randomBytesLength) {
        this.random = new SecureRandom();
        this.randomBytesLength = randomBytesLength;
        this.totalLength = 8 + randomBytesLength; // 8 bytes for timestamp + random bytes
    }
    
    @Override
    public byte[] allocateNext() {
        final var timestamp = System.currentTimeMillis();
        final var randomBytes = new byte[randomBytesLength];
        random.nextBytes(randomBytes);
        
        final var buffer = ByteBuffer.allocate(totalLength);
        buffer.putLong(timestamp);
        buffer.put(randomBytes);
        
        return buffer.array();
    }
    
    @Override
    public boolean isValidCookieId(byte[] cookieId) {
        if (cookieId == null || cookieId.length != totalLength) {
            return false;
        }
        
        // Extract timestamp and check if it's reasonable (not too old or in the future)
        final var timestamp = ByteBuffer.wrap(cookieId, 0, 8).getLong();
        final var now = System.currentTimeMillis();
        final var maxAge = 5 * 60 * 1000; // 5 minutes
        final var maxFuture = 1000; // 1 second in the future
        
        return timestamp >= (now - maxAge) && timestamp <= (now + maxFuture);
    }
    
    @Override
    public int getCookieIdLength() {
        return totalLength;
    }
    
    /**
     * Extracts the timestamp from a cookie ID.
     *
     * @param cookieId the cookie ID byte array
     * @return the timestamp in milliseconds
     * @throws IllegalArgumentException if the cookie ID is invalid
     */
    public long extractTimestamp(byte[] cookieId) {
        if (cookieId == null || cookieId.length != totalLength) {
            throw new IllegalArgumentException("Cookie ID must be exactly " + totalLength + " bytes for TimestampBasedCookieAllocator");
        }
        return ByteBuffer.wrap(cookieId, 0, 8).getLong();
    }
    
    /**
     * Extracts the random bytes from a cookie ID.
     *
     * @param cookieId the cookie ID byte array
     * @return the random bytes portion
     * @throws IllegalArgumentException if the cookie ID is invalid
     */
    public byte[] extractRandomBytes(byte[] cookieId) {
        if (cookieId == null || cookieId.length != totalLength) {
            throw new IllegalArgumentException("Cookie ID must be exactly " + totalLength + " bytes for TimestampBasedCookieAllocator");
        }
        final var randomBytes = new byte[randomBytesLength];
        System.arraycopy(cookieId, 8, randomBytes, 0, randomBytesLength);
        return randomBytes;
    }
}
