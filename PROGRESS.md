# Progress

## Current goal

- Modernize the Swing UI with a coherent vector icon system and clearer editor hierarchy.

## Files touched

- `PROGRESS.md`
- `Java/pom.xml`
- `Java/src/main/java/com/dev/BuildInfo.java`
- `Java/src/main/java/com/dev/Main.java`
- `Java/src/main/java/com/dev/model/ResumeData.java` (deleted)
- `Java/src/main/java/com/dev/service/ResumeService.java`
- `Java/src/main/java/com/dev/service/LatexEnvironment.java`
- `Java/src/main/java/com/dev/service/MissingXeLaTeXException.java`
- `Java/src/main/java/com/dev/util/LatexParser.java`
- `Java/src/main/java/com/dev/util/UITheme.java`
- `Java/src/main/java/com/dev/view/ResumeEditorView.java`
- `Java/src/main/java/com/dev/view/PDFPreviewPanel.java`
- `Java/src/main/java/com/dev/view/OptionsPanel.java`
- `Java/src/main/java/com/dev/view/CreditsPanel.java`
- `Java/src/main/java/com/dev/view/components/IconFactory.java`
- `Java/src/main/java/com/dev/view/components/BannerPanel.java`
- `Java/src/main/java/com/dev/view/components/ModernButton.java`
- `Java/src/main/java/com/dev/view/components/ModernPanel.java`
- `Java/src/main/java/com/dev/view/components/ModernTextField.java`
- `Java/src/main/java/com/dev/view/components/RoundedScrollPane.java`
- `Java/src/main/java/com/dev/view/components/SideMenu.java`
- `Java/src/test/java/com/dev/service/ResumeServiceTest.java`
- `Java/src/test/java/com/dev/service/LatexEnvironmentTest.java`
- `Java/src/test/java/com/dev/BuildInfoTest.java`
- `Java/src/test/java/com/dev/util/LatexParserTest.java`
- `Java/src/test/java/com/dev/util/LatexGeneratorTest.java`
- `Java/src/test/java/com/dev/util/LatexPropertyTest.java`
- `Java/src/test/java/com/dev/util/UIThemeTest.java`
- `Java/src/test/java/com/dev/view/OptionsPanelTest.java`
- `Java/src/test/java/com/dev/view/PDFPreviewPanelTest.java`
- `Java/src/test/java/com/dev/view/components/IconFactoryTest.java`
- `Java/src/test/java/com/dev/view/components/BannerPanelTest.java`
- `Java/src/test/java/com/dev/view/components/ModernButtonTest.java`
- `Java/src/test/java/com/dev/view/components/ModernPanelTest.java`
- `Java/src/test/java/com/dev/view/components/ModernTextFieldTest.java`
- `Java/src/test/java/com/dev/view/components/RoundedScrollPaneTest.java`
- `Java/src/test/java/com/dev/view/components/SideMenuTest.java`
- `Java/src/test/java/com/dev/view/ResumeEditorViewLayoutTest.java`
- `.github/workflows/ci.yml`
- `.github/workflows/release.yml`
- `.github/workflows/code-quality.yml`
- `.github/workflows/performance.yml`
- `README.md`
- `scripts/curriculo-editor-linux`

## Decisions made

- Keep Java/Swing and the current LaTeX-on-disk contract; do not rewrite the app.
- Keep all Swing startup and component creation on the EDT.
- Use platform-selected placement instead of repeatedly recentering/resizing the split pane.
- Render each PDF page in a cancellable worker with an isolated PDFBox document; stale requests cannot replace the current preview.
- Render the 200 DPI magnifier image only when requested, flush discarded images, and coalesce hover repaints to one per 16 ms.
- The Linux launcher only adds Wayland compositor compatibility when the session is Wayland; it preserves a caller-supplied override and requests GTK3.
- CI remains Java 21/Maven based; release now ships the Linux launcher with the JAR and SHA-256 file.
- Maven uses a CI-friendly `revision`: local builds remain `1.3.0-SNAPSHOT`, every CI build uses the commit SHA, and release tags supply the release version.
- The packaged JAR manifest and the two visible version labels read the effective build version; CI never writes or commits a changed POM.
- Before compiling, detect XeLaTeX with a bounded probe. MiKTeX receives its documented on-demand installer flag, so it downloads only missing template packages after user approval.
- The first missing-engine compile offers the OS-specific official installer once per app session, then falls back to showing its HTTPS URL if the desktop browser cannot open it.
- Do not silently bootstrap a system TeX engine: TeX Live user mode cannot install the XeLaTeX binary and Linux package management requires distro-specific privilege handling.
- Shade removes classpath-inapplicable `module-info.class` files, creates one application manifest, omits Maven build metadata, merges Apache notices and preserves all dependency license text.
- Tests avoid real XeLaTeX, package installation, network, and top-level windows; they cover pure command/parser/generator logic, `@TempDir` service state, and bounded Swing components.
- Repair missing bundled fonts inside an existing `fontdir` instead of leaving a partial template tree unrecoverable.
- Component coverage uses deterministic constructors and `BufferedImage` paint smoke checks, never visual pixel snapshots, timers, or desktop integration.
- Use a restrained dark “editorial workstation” palette, logical platform fonts, consistent stroked vector icons, persistent navigation selection, and one primary compile action; preserve all editor actions and LaTeX contracts.
- Keep icons dependency-free and rendered at runtime so Linux/Wayland scaling stays crisp without network or raster assets.
- Stack the PDF title and controls at compact logical widths, hide nonessential action help when space is tight, and derive the initial split from available width instead of a fixed pixel divider.
- Discard any late high-resolution PDF render after the magnifier is disabled; this preserves the existing on-demand memory contract.

## Verified checks

- [x] `rtk mvn --batch-mode --no-transfer-progress verify` from `Java/`: 30 tests passed after worker rendering.
- [x] JFR test profile command completed and `target/tests.jfr` has a valid summary.
- [x] Wayland-session smoke: timed six-second JAR run stayed alive until the expected timeout and wrote a valid JFR; no application error was emitted.
- [x] `rtk sh -n scripts/curriculo-editor-linux`: launcher syntax valid.
- [x] Ruby parsed all GitHub workflow YAML files.
- [x] `rtk git diff --check`: no whitespace errors.
- [x] Earlier performance baseline: 72 DPI A4 preview allocated 1.9 MB; the optional 200 DPI preview added 14.7 MB. Magnifier-off avoids that extra allocation.
- [x] `rtk mvn --batch-mode --no-transfer-progress -f Java/pom.xml -Drevision=1.3.0-dev.0123456789abcdef verify`: 31 tests passed.
- [x] Shaded JAR manifest contains `Implementation-Version: 1.3.0-dev.0123456789abcdef`.
- [x] `rtk mvn --batch-mode --no-transfer-progress -f Java/pom.xml verify`: 34 tests passed, including MiKTeX/TeX Live command coverage.
- [x] `rtk mvn --batch-mode --no-transfer-progress -f Java/pom.xml clean verify`: 34 tests passed with no Shade overlap or module warnings.
- [x] Shaded JAR has one manifest, `META-INF/LICENSE`, `META-INF/NOTICE`, and no `module-info.class`.
- [x] `rtk mvn --batch-mode --no-transfer-progress -f Java/pom.xml verify`: 53 tests passed after the regression-suite expansion.
- [x] `rtk mvn --batch-mode --no-transfer-progress -f Java/pom.xml verify`: 75 tests passed after the second test expansion.
- [x] `rtk mvn --batch-mode --no-transfer-progress -f Java/pom.xml -DskipTests compile`: UI modernization compiles successfully.
- [x] Targeted Swing suite: 24 tests passed for theme, icons, navigation, controls, and PDF preview.
- [x] `rtk mvn --batch-mode --no-transfer-progress -f Java/pom.xml verify`: 84 tests passed with no warnings.
- [x] Xvfb visual smoke at 1600×1000: main editor, selected navigation, vector icons, stacked PDF toolbar, and action hierarchy rendered without overlap or truncated button labels.

## Remaining work

- [x] Move PDF rendering off the EDT and add asynchronous preview regression coverage.
- [x] Provide a Linux/Wayland launcher and attach it to releases.
- [x] Generate unique commit-versioned CI artifacts and tag-versioned release artifacts without CI commits.
- [x] Add bounded XeLaTeX detection, compile logs/timeouts, and MiKTeX on-demand package installation.
- [x] Prompt for the official first-run XeLaTeX installer when the compiler is absent; keep checks on every compile.
- [x] Remove Shade warnings while preserving manifest, service loading, license, and notice resources.
- [x] Expand tests for generator/parser boundaries, jqwik entry round-trips, resource staging, environment commands, options state, and latest PDF preview load.
- [x] Add component smoke tests plus resource fallback, metadata, photo-extension, exact command, multi-page preview, and photo-format coverage.
- [x] Add regression coverage for the new icon set, button variants, navigation selection, responsive split, compact banners, and preview shell.
- [x] Run the full Maven verification and visual smoke the updated Swing shell.
- [ ] Future: make multi-file writes atomic.
- [ ] Environment: repair the local XeLaTeX format (`xelatex.fmt`) before end-to-end compile profiling.
