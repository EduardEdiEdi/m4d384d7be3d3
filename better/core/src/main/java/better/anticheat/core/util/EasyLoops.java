package better.anticheat.core.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Deque;
import java.util.function.Predicate;

/**
 * Utility class providing efficient looping methods as an alternative to Java streams, designed to minimize allocations and improve performance in the anticheat system.
 * These methods are optimized for common collection operations with zero or low overhead.
 */
@UtilityClass
public class EasyLoops {
    /**
     * Determines if at least one element in the specified collection satisfies the given predicate.
     * This method iterates through the collection and returns true upon finding the first matching element, ensuring efficiency.
     *
     * @param <A> the type of elements in the collection
     * @param collection the collection to check; must not be null
     * @param predicate the predicate to test each element against; must not be null
     * @return true if any element matches the predicate, false otherwise
     */
    public <A> boolean anyMatch(final Collection<A> collection, final Predicate<A> predicate) {
        for (A predictedMotion : collection) {
            if (predicate.test(predictedMotion)) return true;
        }

        return false;
    }

    public <A> boolean anyMatch(final A[] collection, final Predicate<A> predicate) {
        for (A predictedMotion : collection) {
            if (predicate.test(predictedMotion)) return true;
        }

        return false;
    }

    public <A> int count(final Iterable<A> collection, final Predicate<A> predicate) {
        int count = 0;

        for (A predictedMotion : collection) {
            if (predicate.test(predictedMotion)) count++;
        }

        return count;
    }

    /**
     * Verifies if all elements in the specified collection satisfy the given predicate.
     * This method iterates through the collection and returns false upon finding the first non-matching element, optimizing for early termination.
     *
     * @param <A> the type of elements in the collection
     * @param collection the collection to check; must not be null
     * @param predicate the predicate to test each element against; must not be null
     * @return true if all elements match the predicate, false otherwise
     */
    public <A> boolean allMatch(final Iterable<A> collection, final Predicate<A> predicate) {
        for (A predictedMotion : collection) {
            if (!predicate.test(predictedMotion)) return false;
        }

        return true;
    }

    /**
     * Filters elements from the input collection that match the predicate and adds them to the provided output ArrayList.
     * This method reuses the output list to avoid unnecessary allocations, promoting efficiency.
     *
     * @param <A> the type of elements in the collection
     * @param in the input collection to filter; must not be null
     * @param out the output ArrayList to add matching elements; must not be null
     * @param predicate the predicate to test each element against; must not be null
     * @return the modified output ArrayList containing the filtered elements
     */
    public <A> @NotNull List<A> filterArrayList(final Collection<A> in, final List<A> out, final Predicate<A> predicate) {
        for (A predictedMotion : in) {
            if (predicate.test(predictedMotion)) out.add(predictedMotion);
        }

        return out;
    }

    /**
     * Filters elements from the input collection that match the predicate and adds them to the provided output Queue.
     * This method reuses the output queue to avoid unnecessary allocations, promoting efficiency.
     *
     * @param <A> the type of elements in the collection
     * @param in the input collection to filter; must not be null
     * @param out the output Queue to add matching elements; must not be null
     * @param predicate the predicate to test each element against; must not be null
     * @return the modified output Queue containing the filtered elements
     */
    public <A> @NotNull Queue<A> filterQueue(final Collection<A> in, final Queue<A> out, final Predicate<A> predicate) {
        for (A predictedMotion : in) {
            if (predicate.test(predictedMotion)) out.add(predictedMotion);
        }

        return out;
    }

    /**
     * Returns the first element in the collection that matches the specified predicate, or null if no element matches.
     * This method iterates through the collection and returns the first matching element for efficient lookup.
     *
     * @param <A> the type of elements in the collection
     * @param collection the collection to search; must not be null
     * @param predicate the predicate to test each element against; must not be null
     * @return the first element that matches the predicate, or null if no match is found
     */
    public <A> A findFirst(final Collection<A> collection, final Predicate<A> predicate) {
        for (A item : collection) {
            if (predicate.test(item)) {
                return item;
            }
        }
        return null;
    }

    public <A> A findLast(final Deque<A> collection, final Predicate<A> predicate) {
        final Iterator<A> iterator = collection.descendingIterator();
        while (iterator.hasNext()) {
            A item = iterator.next();
            if (predicate.test(item)) {
                return item;
            }
        }
        return null;
    }
}
