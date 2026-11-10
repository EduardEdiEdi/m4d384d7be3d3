package better.anticheat.core.check.impl.chat;

import better.anticheat.core.BetterAnticheat;
import better.anticheat.core.check.Check;
import better.anticheat.core.check.CheckInfo;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTabComplete;

/**
 * This check looks for impossible tab completions.
 */
@CheckInfo(name = "ImpossibleCompletion", category = "chat")
public class ImpossibleCompletionCheck extends Check {

    public ImpossibleCompletionCheck(BetterAnticheat plugin) {
        super(plugin);
    }

    @Override
    public void handleReceivePlayPacket(PacketPlayReceiveEvent event) {

        /*
         * This check verifies that you have attempted to type something before attempting a tab completion.
         * You cannot tab complete an empty string.
         */

        if (event.getPacketType() != PacketType.Play.Client.TAB_COMPLETE) return;
        WrapperPlayClientTabComplete wrapper = new WrapperPlayClientTabComplete(event);
        if (wrapper.getText().isEmpty()) fail();
    }
}
