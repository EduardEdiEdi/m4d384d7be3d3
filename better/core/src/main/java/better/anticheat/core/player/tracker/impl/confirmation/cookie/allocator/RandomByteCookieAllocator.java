package better.anticheat.core.player.tracker.impl.confirmation.cookie.allocator;

import better.anticheat.core.player.tracker.impl.confirmation.cookie.CookieIdAllocator;

import java.security.SecureRandom;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A cookie ID allocator that generates random byte sequences.
 * This provides better security by making cookie IDs unpredictable.
 */
public class RandomByteCookieAllocator implements CookieIdAllocator {
    
    private final SecureRandom random;
    private final int cookieLength;
    private final Set<String> generatedIds; // Track generated IDs to avoid duplicates
    private final int maxRetries;
    
    public RandomByteCookieAllocator() {
        this(8, 100); // Default 8 bytes, max 100 retries for uniqueness
    }
    
    public RandomByteCookieAllocator(int cookieLength) {
        this(cookieLength, 100);
    }
    
    public RandomByteCookieAllocator(int cookieLength, int maxRetries) {
        this.random = new SecureRandom();
        this.cookieLength = cookieLength;
        this.generatedIds = ConcurrentHashMap.newKeySet();
        this.maxRetries = maxRetries;
    }
    
    @Override
    public byte[] allocateNext() {
        byte[] cookieId = new byte[cookieLength];
        String idString;
        int retries = 0;
        
        do {
            random.nextBytes(cookieId);
            idString = bytesToHexString(cookieId);
            retries++;
        } while (generatedIds.contains(idString) && retries < maxRetries);
        
        generatedIds.add(idString);
        
        // Clean up old IDs if the set gets too large (prevent memory leaks)
        if (generatedIds.size() > 10000) {
            generatedIds.clear();
        }
        
        return cookieId;
    }
    
    @Override
    public boolean isValidCookieId(byte[] cookieId) {
        return cookieId != null && cookieId.length == cookieLength;
    }
    
    @Override
    public int getCookieIdLength() {
        return cookieLength;
    }
    
    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
