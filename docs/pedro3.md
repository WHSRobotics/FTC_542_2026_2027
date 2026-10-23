# Pedro 3 setup for 542

The robot's Pedro configuration is in `TeamCode/src/main/java/org/whitneyrobotics/ftc/teamcode/pedro/Constants.java`. `Tuning.java` exposes the AutoTune procedures; the procedure sources came from the [Pedro quickstart](https://github.com/Pedro-Pathing/Quickstart/tree/8f6804c) (commit `8f6804c`). Dependencies are pinned to `revhub:3.0.2` and `tuning:1.0.1`. The TeleOps use `PedroDrive` to keep the existing robot/field centric toggle and speed setting.

## Before driving

1. Check the active Robot Controller configuration. The old Pedro code used `fl` (front left), `fr` (back left), `bl` (front right), and `br` (back right). The manual-drive tests instead use `fL`, `bL`, `fR`, and `bR`. These are configuration names, not physical hub port numbers. Update `Constants.java` to match the *actual* wheel positions and directions.
2. Verify the Pinpoint is named `pinpoint`, uses goBILDA 4-bar pods, and has its forward pod in X and lateral pod in Y. The old offsets and reversed pod directions are a starting point, not a substitute for measurement.
3. With the robot supported, verify each wheel's direction using the Mecanum tuner before driving on the floor.

## Tune

Connect to the Robot Controller and open `http://192.168.43.1:10158`. Run Mecanum, Pinpoint, Foresight, then Tests. Copy each procedure's generated Java configuration into `Constants.java` before advancing. In the localization test, forward motion should increase X and left strafe should increase Y.

The four named autonomous OpModes inherited empty path chains from the previous code. They currently initialize Pedro localization and show pose, but do not drive a route. Add measured paths using the Pedro 3 `Paths.line()` / `Paths.curve()` API before match use.

References: https://pedropathing.com/docs/pathing/tuning/ and https://pedropathing.com/docs/pathing/guide/

## Migration handoff (2026-10-22)

Local branch: `pedro3-complete`; merge commit joins `master` (`cc73aec`) and the incomplete `pedro3conv` (`4bdd61d`). The prior Outtake edits remain uncommitted and separate. Pedro procedure files came from the other branch; they match quickstart `8f6804c` apart from package names and minor whitespace.

Claude collaboration used the existing subscription login. The persistent brainstorming/review session was `58a319a1-66fc-4bde-baad-3b375684023c`. A separate execution session was `a90b769d-2a6c-46ad-8de0-cad199040668`. Its exact task prompt was:

> You are the scoped implementation collaborator for FTC Pedro 3 migration. Own ONLY TeamCode/src/main/java/org/whitneyrobotics/ftc/teamcode/pedro/Constants.java, TeamCode/src/main/java/org/whitneyrobotics/ftc/teamcode/pedro/Tuning.java, and build.dependencies.gradle. Do not edit other files, commit, or switch branches. In cwd repo, use official Pedro 3 docs at pedropathing.com and the freshly cloned official quickstart at /tmp/pedro-quickstart-542. Implement a working Follower factory with Mecanum, Pinpoint, Foresight configs, and only needed Mecanum/Pinpoint/Foresight/Tests tuners. Preserve current old pedroPathing/Constants.java hardware name/direction/Pinpoint pod/offset baseline exactly while mapping old -> new APIs; flag uncertainties in response. Update dependencies to match quickstart revhub 3.0.2 tuning 1.0.1. Avoid invented tuning values; use defensible defaults with explicit need for hardware tuning. Do not touch the previous user Outtake edits. Read files as needed; after editing, independently verify owned files and report exact commands/results. Our parallel owner handles callers. Keep minimal, neat.

The Claude CLI reached its 20-turn limit after editing those three files, so it returned no final verification report. Codex reviewed the edits, corrected the Pinpoint offset mapping against the Pedro 2 and 3 source, set manual brake mode, and replaced zero path-braking coefficients with a baseline calculated from the old deceleration values. `./gradlew :TeamCode:assembleDebug --console=plain` passed twice: `BUILD SUCCESSFUL in 23s` and, after the corrections, `BUILD SUCCESSFUL in 1s` (52 actionable tasks; 9 executed, 43 up-to-date on the final run). Java 8 source/target deprecation warnings remain. `git diff --cached --check` passed before commit. No robot hardware was available for motor-direction, Pinpoint, or path tuning tests.
