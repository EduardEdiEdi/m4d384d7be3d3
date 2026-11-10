package better.anticheat.core.util.math;

import java.util.List;

/**
 * Credit to Artemis. Allowed to use via Artemis MIT license.
 * https://github.com/artemisac/artemis-minecraft-anticheat/blob/master/ac.artemis.core/src/main/java/ac/artemis/core/v4/utils/graphing/GraphUtil.java
 */
public class GraphUtil {

    public record GraphResult(String graph, int positives, int negatives) {}

    public static GraphResult getGraph(List<Float> values) {
        StringBuilder graph = new StringBuilder();

        float largest = 0;

        for (float value : values) {
            if (value > largest)
                largest = value;
        }

        int GRAPH_HEIGHT = 2;
        int positives = 0, negatives = 0;

        for (int i = GRAPH_HEIGHT - 1; i > 0; i -= 1) {
            StringBuilder sb = new StringBuilder();

            for (float index : values) {
                float value = GRAPH_HEIGHT * index / largest;

                if (value > i && value < i + 1) {
                    ++positives;
                    sb.append(String.format("%s+", "&2"));
                } else {
                    ++negatives;
                    sb.append(String.format("%s-", "&4"));
                }
            }

            graph.append(sb.toString());
        }

        return new GraphResult(graph.toString(), positives, negatives);
    }
}
