package better.anticheat.core.util.entity;

import com.github.retrooper.packetevents.util.Vector3d;
import lombok.Data;

@Data
public class RayCastResult {
    private final Vector3d vector;
    private final double distance;
    private final boolean collided;
}
