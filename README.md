# Kestrel

A report-only Paper plugin made of two modified open-source anticheat code bases:

| Module | Upstream | License |
|---|---|---|
| interaction (`light/`) | [LightAntiCheat](https://github.com/Vekster/LightAntiCheat) by Vekster, 1.2.9.5 as maintained in [Christiano9590/Updated-LightAntiCheat](https://github.com/Christiano9590/Updated-LightAntiCheat) | AGPL-3.0 |
| combat (`better/`) | [BetterAnticheat](https://github.com/amnoah/BetterAnticheat) by amnoah and contributors | AGPL-3.0 |

This repository is the complete corresponding source of the modified versions, published under the same license
(see `LICENSE`, AGPL-3.0). All credit for the detection logic goes to the original authors.

## What it does

Both modules run their checks and fire one Bukkit event, `kestrel.api.DetectionEvent`
(player, module, category, check, level, detail), on the main thread. Another plugin decides what to do with it.
Kestrel itself never alerts, kicks, bans, sets back, cancels movement or changes damage.

## Changes from upstream

LightAntiCheat:
* movement, timer, reach, velocity, air-place, block-place, BlockBreakB and the bad-packet checks removed
  (only killaura, criticals, autoclicker, fast place/break, ghost break, block break A, scaffold, MorePackets B,
  inventory sorting/item swap, auto-bot and skin blinker remain)
* commands, updater, metrics, the invalid-ping kick and the public violation/punishment events removed
* `ViolationHandler` reports through a callback instead of alerting/punishing; every check's punishment is off
* bypass permissions disabled by default; alerts, logs and bStats off
* constants renamed between server versions (Luck of the Sea, Punch, Haste, TNT, short grass) are looked up at
  runtime (`util/Legacy`) so the module loads on current servers

BetterAnticheat:
* only the Paper platform is kept (Spigot, Sponge, Velocity, distribution and model modules removed)
* commands, punishments, alerts, verbose output, ML/CML checks, mitigation (damage changes) and the Grim hook removed
* a subset of checks kept: HiddenChat, ImpossibleCompletion, ImpossibleMessage, InvalidInteractionPosition,
  NoSwingCombat, SelfHit, AimStabilization, CombatAcceleration, LinearAimDeviation, MicroAimMovement, LargeName
* `DataBridge.report(...)` replaces alerts; the confirmation tracker never disconnects a player (an unanswered
  confirmation is dropped), and a module error never closes the connection
* runtime libraries from Maven Central only

Host (`host/`): `KestrelPlugin` starts both modules and turns their reports into `DetectionEvent`s (throttled to one
per player and check every 200 ms).

## Build

Java 21, Gradle wrapper included. The interaction module compiles against the Spigot 1.8.8 API: build
`spigot-1.8.8-R0.1-SNAPSHOT-latest.jar` with Spigot's BuildTools and put it in `light/comp/` (not redistributed here).

```
./gradlew shadowJar   # build/libs/Kestrel-1.0.0.jar
```

Needs PacketEvents on the server.
