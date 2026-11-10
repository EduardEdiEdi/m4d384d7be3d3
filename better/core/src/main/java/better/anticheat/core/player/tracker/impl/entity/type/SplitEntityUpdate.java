package better.anticheat.core.player.tracker.impl.entity.type;

import lombok.Data;
import wtf.spare.sparej.incrementer.IntIncrementer;

@Data
public class SplitEntityUpdate {
    private final IntIncrementer flyings = new IntIncrementer(0);
    private final EntityData data;
    private final EntityTrackerState oldState;
    private final double x;
    private final double y;
    private final double z;
}
