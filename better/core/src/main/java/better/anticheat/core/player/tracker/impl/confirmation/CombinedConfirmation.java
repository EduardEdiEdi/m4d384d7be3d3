package better.anticheat.core.player.tracker.impl.confirmation;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import wtf.spare.sparej.SimpleFuture;
import wtf.spare.sparej.incrementer.IntIncrementer;

import java.util.function.Consumer;

@EqualsAndHashCode
@ToString
@Getter
@AllArgsConstructor
public class CombinedConfirmation {
    private SimpleFuture<ConfirmationState> onBegin;
    private wtf.spare.sparej.SimpleFuture<ConfirmationState> onAfterConfirm;
    /**
     * 0 = Sent,
     * 1 = Begun
     * 2 = Confirmed
     */
    private IntIncrementer state = new IntIncrementer(0);

    public CombinedConfirmation(final SimpleFuture<ConfirmationState> onBegin, final SimpleFuture<ConfirmationState> onAfterConfirm) {
        this.onBegin = onBegin;
        this.onAfterConfirm = onAfterConfirm;
    }

    /**
     * Run the given runnable once the confirmation has been sent to the player.
     * Note that this does not necessarily mean that the player has confirmed the packet.
     *
     * @param consumer the runnable to be ran.
     */
    public CombinedConfirmation onBegin(final Consumer<ConfirmationState> consumer) {
        this.onBegin.addListener(consumer);

        return this;
    }

    /**
     * Run the given runnable once the confirmation has been sent to the player.
     * Note that this does not necessarily mean that the player has confirmed the packet.
     *
     * @param consumer the runnable to be ran.
     */
    public CombinedConfirmation onBegin(final Runnable consumer) {
        this.onBegin = this.onBegin.addListener((a) -> consumer.run());

        return this;
    }

    public CombinedConfirmation onAfterConfirm(final Consumer<ConfirmationState> consumer) {
        this.onAfterConfirm = this.onAfterConfirm.addListener(consumer);

        return this;
    }

    public CombinedConfirmation onAfterConfirm(final Runnable consumer) {
        this.onAfterConfirm = this.onAfterConfirm.addListener((a) -> consumer.run());
        return this;
    }
}
