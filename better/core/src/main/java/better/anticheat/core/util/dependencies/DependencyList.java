package better.anticheat.core.util.dependencies;

import lombok.experimental.UtilityClass;

/*
 * Modified: only what the remaining code needs and the server does not already provide (fastutil, snakeyaml and
 * guava come with Paper; sparej is bundled in the jar). Resolved from Maven Central.
 */
@UtilityClass
public class DependencyList {
    public static final String[] DEPENDENCIES = new String[]{
            "org.apache.fory:fory-core:0.11.2",
    };
}
