package better.anticheat.core.player.tracker.impl.confirmation.cookie.allocator;

import better.anticheat.core.player.tracker.impl.confirmation.cookie.CookieIdAllocator;
import wtf.spare.sparej.incrementer.LongIncrementer;

import java.nio.ByteBuffer;

/**
 * A cookie ID allocator that generates sequential long values
 * and converts them to 8-byte arrays. This maintains compatibility
 * with the original implementation.
 */
public class SequentialLongCookieAllocator implements CookieIdAllocator {
    
    private final LongIncrementer incrementer;
    
    public SequentialLongCookieAllocator() {
        this.incrementer = new LongIncrementer(Long.MIN_VALUE);
    }
    
    public SequentialLongCookieAllocator(long startValue) {
        this.incrementer = new LongIncrementer(startValue);
    }
    
    @Override
    public byte[] allocateNext() {
        final long id = incrementer.increment();
        return ByteBuffer.allocate(8).putLong(id).array();
    }
    
    @Override
    public boolean isValidCookieId(byte[] cookieId) {
        // Any 8-byte array is potentially valid for this allocator
        return cookieId != null && cookieId.length == 8;
    }
    
    @Override
    public int getCookieIdLength() {
        return 8;
    }
    
    /**
     * Extracts the long value from a cookie ID byte array.
     * 
     * @param cookieId the 8-byte cookie ID
     * @return the long value
     * @throws IllegalArgumentException if the cookie ID is not 8 bytes
     */
    public long extractLongValue(byte[] cookieId) {
        if (cookieId == null || cookieId.length != 8) {
            throw new IllegalArgumentException("Cookie ID must be exactly 8 bytes for SequentialLongCookieAllocator");
        }
        return ByteBuffer.wrap(cookieId).getLong();
    }
}
