package better.anticheat.core.player.tracker.impl.confirmation.performance;

import better.anticheat.core.player.tracker.impl.confirmation.ConfirmationState;
import better.anticheat.core.player.tracker.impl.confirmation.ConfirmationType;
import better.anticheat.core.util.EasyLoops;
import it.unimi.dsi.fastutil.objects.*;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SetPerformanceTest {

    // Record to hold the results of a single test run
    private record TestResult(String setName, long setupTime, long findTime) {}

    // Record to hold the configuration for a test
    private record TestConfig(String name, Supplier<Set<ConfirmationState>> supplier) {}

    public static void main(String[] args) {
        // Test parameters
        final int testRuns = 500;
        final int setSize = 600;
        final int findOperations = 10000;

        System.out.println("=== Starting Performance Test ===");
        System.out.printf("Set Size: %,d | Find Operations: %,d | Test Runs: %d%n%n",
                setSize, findOperations, testRuns);

        // Define all set implementations to be tested
        List<TestConfig> configs = new ArrayList<>(List.of(
                new TestConfig("ObjRBTreeSet", ObjectRBTreeSet::new),
                new TestConfig("ObjAVLTreeSet", ObjectAVLTreeSet::new),
                new TestConfig("ObjArraySet", () -> new ObjectArraySet<>(setSize * 2)),
                new TestConfig("ObjOpHashSet", () -> new ObjectOpenHashSet<>(setSize * 2)),
                new TestConfig("ObjOpLnHashSet", () -> new ObjectLinkedOpenHashSet<>(setSize * 2)),
                new TestConfig("HashSet", () -> new HashSet<>(setSize * 2)),
                new TestConfig("TreeSet", TreeSet::new),
                new TestConfig("LinkedHashSet", () -> new LinkedHashSet<>(setSize * 2))
        ));

        // Warmup phase
        System.out.println("--- Warming up JVM ---");
        configs.forEach(config -> runTest(config, setSize, findOperations));
        System.out.println("--- Warmup Complete ---\n");

        // Main test phase
        final List<TestResult> allResults = new ArrayList<>(500);
        for (int i = 1; i <= testRuns; i++) {
            System.out.printf("--- Test Run %d --- %n", i);
            Collections.shuffle(configs); // Randomize order to prevent bias
            for (TestConfig config : configs) {
                allResults.add(runTest(config, setSize, findOperations));
            }
        }

        // Aggregate and print results
        System.out.println("\n=== Aggregated Results ===");
        printSummary(allResults, testRuns);
    }

    private static TestResult runTest(TestConfig config, int setSize, int findOperations) {
        System.gc();
        long startSetup = System.nanoTime();
        Set<ConfirmationState> set = config.supplier.get();
        long now = System.currentTimeMillis();

        for (int i = 0; i < setSize; i++) {
            set.add(new ConfirmationState(i, ConfirmationType.KEEPALIVE, now + i, true));
            set.add(new ConfirmationState(i, ConfirmationType.COOKIE, now + i, true));
        }
        long endSetup = System.nanoTime();

        long startFind = System.nanoTime();
        for (int i = 0; i < findOperations; i++) {
            final long finalNow = now;
            EasyLoops.findFirst(set, c -> c.getType() == ConfirmationType.KEEPALIVE && finalNow - c.getTimestamp() < 55);
        }
        long endFind = System.nanoTime();

        // System.out.printf("  %-15s -> Setup: %6.3f ms, Find: %6.3f ms%n", config.name(), (endSetup - startSetup) / 1e6, (endFind - startFind) / 1e6);

        return new TestResult(config.name(), endSetup - startSetup, endFind - startFind);
    }

    private static void printSummary(List<TestResult> results, int testRuns) {
        Map<String, List<TestResult>> groupedResults = results.stream()
                .collect(Collectors.groupingBy(TestResult::setName));

        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.printf("%-15s | %-25s | %-25s | %-15s%n", "Set Type", "Setup Time (ms)", "Find Time (ms)", "Total Time (ms)");
        System.out.printf("%-15s | %-8s %-8s %-8s | %-8s %-8s %-8s | %-15s%n", "", "Avg", "Min", "Max", "Avg", "Min", "Max", "Avg");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        groupedResults.entrySet().stream()
                .map(entry -> {
                    String name = entry.getKey();
                    List<Long> setupTimes = entry.getValue().stream().map(TestResult::setupTime).toList();
                    List<Long> findTimes = entry.getValue().stream().map(TestResult::findTime).toList();

                    double avgSetup = setupTimes.stream().mapToLong(l -> l).average().orElse(0) / 1e6;
                    double minSetup = setupTimes.stream().mapToLong(l -> l).min().orElse(0) / 1e6;
                    double maxSetup = setupTimes.stream().mapToLong(l -> l).max().orElse(0) / 1e6;

                    double avgFind = findTimes.stream().mapToLong(l -> l).average().orElse(0) / 1e6;
                    double minFind = findTimes.stream().mapToLong(l -> l).min().orElse(0) / 1e6;
                    double maxFind = findTimes.stream().mapToLong(l -> l).max().orElse(0) / 1e6;

                    return new AbstractMap.SimpleEntry<>(name, new double[]{avgSetup, minSetup, maxSetup, avgFind, minFind, maxFind});
                })
                .sorted(Comparator.comparingDouble(e -> e.getValue()[0] + e.getValue()[3])) // Sort by avg total time
                .forEach(entry -> {
                    double[] stats = entry.getValue();
                    System.out.printf("%-15s | %8.3f %8.3f %8.3f | %8.3f %8.3f %8.3f | %15.3f%n",
                            entry.getKey(), stats[0], stats[1], stats[2], stats[3], stats[4], stats[5], stats[0] + stats[3]);
                });
        System.out.println("----------------------------------------------------------------------------------------------------------");
    }
}
