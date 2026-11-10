package better.anticheat.core.util.type.world;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlockPos {
    private int x;
    private int y;
    private int z;
}
