# AGENTS.md

JavaFX audio tag editor (MP3/MP4/FLAC/OGG/WMA). Java 25, Maven. The project switched back from Gradle to Maven (commit `c2ad112`) — don't re-introduce Gradle. There is no lint/format gate; `mvn test` is the only CI check.

## Build / run
- Needs a JDK 25 toolchain (OpenJFX 25.0.3 is declared in `pom.xml`; the compiler uses `--release 25`). All workflows use zulu 25 — do not downgrade to 21.
- `mvn test` — unit tests; this is the CI gate (GitHub Actions runs it on push to `master`).
- `mvn clean package` — full build: compiles, runs `jlink` to emit a JRE into `target/jlink/jre`, and via OS-activated profiles produces the release archive (`linux-dist` → `ntag-<ver>-linux_bin.tar.gz`, `windows-dist` → `...-win_bin.zip` + exploded `...-win_bin/` dir for Inno Setup, `macos-dist` → `...-macos_bin.tar.gz` with a self-contained `NTag.app`).
- `mvn javafx:run` — launch the desktop app in dev (main class `ntag.NTag`). Same as the IntelliJ "NTag [run]" config.
- Releases: pushing a `v*` tag triggers three independent workflows (`release-linux.yml`, `release-windows.yml`, `release-macos.yml`). Each builds + verifies its platform archive and attaches it to a single draft release (the first to finish creates it via `gh release create --draft || true`, the others `gh release upload --clobber`).

## Testing gotchas
- Surefire is configured with `<groups>Unit</groups>` (see `pom.xml`). **Every test class must be annotated `@Tag(Category.Unit)`** (`ntag.Category`), otherwise it is silently skipped and `mvn test` still reports success. This is the easiest mistake to make here.
- Tests touching audio files extend `ntag.AbstractAudioFileTest`, which in `@BeforeEach` points `NTagProperties.instance().setHomeDir()` at a JUnit `@TempDir`. `NTagProperties` is a singleton — tests must isolate home-dir state and never depend on the developer's real `~/.config/ntag` settings.
- Test fixtures live in `src/test/resources`: `sample.flac`, `sample.wma`, `sample.m4a`, `sample_id3v23.mp3`, `sample_id3v24.mp3`, `artwork.jpg`, `sample.ini`. Access them only through the `get*Sample()`/`getArtwork()` helpers on `AbstractAudioFileTest` (resources are resolved via classloader).
- Tests use JUnit 5 + AssertJ.

## Architecture
- Entry points: `ntag.NTag` (JavaFX `Application`) and `ntag.Batch` (standalone `main` for bulk tag rewrites). Both take `-h/--home` and `-p/--portable` CLI options.
- `ntag.model` — domain: `TagFile`, `Genre`, `AudioFormat`, `ArtworkTag`, and per-format frame maps (`ID3v2Frames`, `Atoms`, `ASF`, `VorbisComments`).
- `ntag.io` — all file I/O; `JAudiotaggerUtil` wraps `jaudiotagger` (the only audio-tagging dependency). Also `io.ini` (`IniFile`), `io.log` (custom `java.util.logging` setup), `io.util` helpers.
- `ntag.fx` — JavaFX UI, MVVM: `fx.scene` controllers + ViewModels, `fx.scene.control.tableview` (custom cells/columns), `fx.validator`, `fx.util`, `fx.scene.dialog`. View wiring is in `src/main/resources/fxml/`.
- `ntag.task` — `javafx.concurrent.Task` subclasses for batch read/write/rename/artwork work.

## Conventions
- Every Java file carries a GPL-3.0 license header (`Copyright <year>, Nico Rittstieg`); preserve it on new and modified files.
- UI strings are externalized in `src/main/resources/bundles/` (`ntag.properties` default, `ntag_en.properties`, `ntag_de.properties`) — **add every new string to all three**. Loaded via `Resources.getResourceBundle("ntag")`. Keep the three bundles key-aligned; `mvn test` won't catch a missing key (falls back to the default bundle), so check parity after edits. `ntag_de.properties` must stay ASCII — use `\uXXXX` escapes for umlauts etc. (`\u00E4`, not a raw `ä`).
- End-user help lives in `tip_*` bundle keys (one per explanatory tooltip), referenced from FXML as `<tooltip><Tooltip text="%tip_xxx"/></tooltip>` or set programmatically via `Resources.get("ntag", "tip_xxx")`. Every dialog/control should carry a tooltip explaining what it does. The `.tooltip` wrap/max-width rule lives in **both** `src/main/resources/ntag.css` and `ntag_dark.css` — keep them in sync.
- Dialog help text that is more than a one-liner should be a dedicated `msg_*` key passed to `Alert.setContentText(...)` (see the Number Tracks confirmation in `NTagWindowController`).
- The wiki (`wiki/`) is its **own separate git repository** (nested repo, gitignored from the main repo). Wiki changes are committed with `git -C wiki ...` and do not belong in the main repo's commits.
- Logging uses `java.util.logging` (custom formatter/handler in `ntag.io.log`), configured by `ntag_logging.properties` — not slf4j/log4j.
- `wiki/`, `.idea/`, `.run/`, `bin/`, `build/`, `target/` are gitignored; `etc/` and `doc/` hold distribution/runtime copies (launchers, logging props, README).
- App icon: source of truth is `tools/icon/IconGenerator.java` (dependency-free java.awt). Regenerate all icon assets with `javac -d tools/icon/build tools/icon/IconGenerator.java && java -cp tools/icon/build IconGenerator tools/icon/ntag.iconset && iconutil -c icns tools/icon/ntag.iconset -o etc/NTag.icns`. This writes `src/main/resources/icons/ntag.png`, `etc/ntag.png`, `etc/ntag.ico`, `etc/NTag.icns`, `etc/ntag.svg`. Commit the regenerated binaries; `tools/icon/build/` and `tools/icon/ntag.iconset/` are gitignored.
- Release checksums: linux/macos use `sha256sum`/`shasum -a 256`
