package better.anticheat.core.util.type.world;

import com.github.retrooper.packetevents.protocol.world.states.type.StateType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StateTypesUtil {
    public static final List<StateType> fences = new CopyOnWriteArrayList<>();
    public static final List<StateType> fenceGates = new CopyOnWriteArrayList<>();
    public static final List<StateType> walls = new CopyOnWriteArrayList<>();

    static {
        for (StateType value : com.github.retrooper.packetevents.protocol.world.states.type.StateTypes.values()) {
            final var fence = value.getName().toUpperCase().contains("FENCE");
            final var gate = value.getName().toUpperCase().contains("GATE");
            if (fence) {
                if (gate) {
                    fenceGates.add(value);
                } else {
                    fences.add(value);
                }
            } else if (value.getName().toUpperCase().contains("WALL")) {
                walls.add(value);
            }
        }
    }
}
