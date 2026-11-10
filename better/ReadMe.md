# BetterAnticheat

**BEFORE INSTALLING, PLEASE READ:** 

At this point, BetterAnticheat is in an early developmental state. It is not confirmed to be stable and should be used
with caution.

BetterAnticheat is an auxiliary anticheat. BetterAnticheat does not aim to and will not catch cheats like flight, speed,
reach, etc. BetterAnticheat exists to catch more niche cheats that many anticheats do not catch on their own, meaning it
should be run alongside another anticheat.

It is also designed to be lightweight, platform-independent, compatible with other anticheat systems, and most checks 
are designed with a 0-buffer system - meaning if a check flags it means that a player is 100% cheating. Here is how
BetterAnticheat accomplishes this:
- Lag compensation. BA knows how the client perceives the world at any given time and can account for it.
- Cookies. BA uses the cookie packet for compensation as to not interfere with Ping/Pong usage.
- Runtime command registration. BA commands can have their paths altered live and can be enabled/disabled.
- Industry-grade design. BA is designed by anticheat developers with significant experience and history.
- Machine learning. BA uses machine learning to establish a trust level for users in combat.

### This project and its collaborators use the following regularly and are thankful for their maintenance:
- [PacketEvents](https://github.com/retrooper/packetevents)
- [sharkbyte-configuration](https://github.com/amnoah/sharkbyte-configuration)

A public test server exists at `192.18.159.249` for clients 1.21.4 and above, but it is not always online due to being
used for development too.

NOTE: Please stop claiming this is a local ip address! It is not. You are thinking of IPs in the `192.168.0.0` - 
`192.168.255.255` range.

## Requirements

This plugin is designed for 1.21.4+ on Spigot, Paper/Folia, Sponge, and Velocity.

It requires [PacketEvents](https://github.com/retrooper/packetevents)

On the Spigot and Paper/Folia platforms, it supports [BetterReload](https://modrinth.com/plugin/betterreload). This is an optional dependency.

## Checks

BetterAnticheat has the following checks in the following categories:

### Chat

These checks look for issues with chatting. These may catch macros and chat automation tools.

- HiddenChat | Sending chat messages with the chat box hidden (often macros).
- ImpossibleCompletion | Tab completing invalid messages.
- ImpossibleMessage | Sending empty or invalidly formatted messages.

### Combat

These checks look for issues with combat. These may catch a variety of combat cheats.

- ActionInteractOrder | Sending an EntityAction packet prior to an InteractEntity in a tick.
- DualClick | Sending simultaneous left and right clicks.
- InvalidInteractionPosition | Invalid client-calculated hitbox interactions.
- InvalidReleaseValues | Releasing a used item with values filled out.
- InvalidUseActions | Attacking or placing while using an item.
- MultipleAction | Changing sneak or sprint status multiple times in a tick.
- MultipleHit | Hitting multiple entities in a tick.
- NoSwingCombat | Attacking an entity without sending an arm swing packet.
- SelfHit | Attacking yourself.
- SlotInteractOrder | Sending a SlotChange prior to InteractEntity packet in a tick.

### Dig

These checks look for issues with digging. These may catch nuker, fast break, and other cheats.

- DigBlockFacePosition | Digging block faces which cannot be seen.
- DigOrder | Sending wrongful dig stages.
- MultiBreak | Digging multiple blocks at once.
- RepeatedDig | Sending multiple dig packets within a tick.

### Flying

These checks look for issues with flying packets. These may catch position/rotation alterations and related cheats.

- ArtificialFlying | Faking flying packets.
- ArtificialPosition | Sending impossibly small position changes.
- FlyingSequence | Not sending flying packets within 20 ticks.
- ImpossiblePosition | Sending position values that are impossible.
- ImpossibleRotation | Sending rotation values that are impossible.
- RepeatedRotation | Sending repeated rotation values.
- RepeatedSteer | Sending steer packets with no corresponding rotation change.

### Heuristic

These checks use statistics and observed behavior to identify cheats. Unlike other checks, these are not built on
defined behavior and theoretically could be false flagged by perfect player behavior. As such, I'd recommend that these 
should be used as an indicator to watch players and, at most, kick them rather than issuing more permanent punishments.

- CombatAcceleration | Constant acceleration during combat.
- LinearAimDeviation | Linear regression-based aim check.

### Misc

These checks are a variety that don't fit into other categories.

- ImpossibleHorseJump | Sending mathematically wrong horse jumps.
- ImpossibleSlot | Accessing slots which are not real.
- LargeName | Renaming an item an excessively long name.
- MultipleSlot | Changing slots multiple times in a tick.
- SmallRender | Having a render distance less than 2.

### Packet

These checks look for general packet issues. These may catch packet order alterations.

- Balance | Checks for accelerated game speeds (WIP).
- Post | Sending packets in the wrong order.

### Place

These checks look for issues with block placement. These may catch scaffold, fast place, ghost hand, and other cheats.

- CursorPosition | Invalid client-calculated block raycasts.
- PlaceBlockFacePosition | Placing on block faces which cannot be seen.

# Documentation

For the documentation home, please visit [here](https://github.com/amnoah/BetterAnticheat/wiki/).

For information on how to hide BetterAnticheat, please visit [here](https://github.com/amnoah/BetterAnticheat/wiki/Hiding-BetterAnticheat).

For information on how to use BetterAnticheat, please visit [here](https://github.com/amnoah/BetterAnticheat/wiki/Using-BetterAnticheat).

# Support

For general support, please join my [Discord server](https://discord.gg/ey9uTg3hcy).

For issues with the project, please open an issue in the issues tab.