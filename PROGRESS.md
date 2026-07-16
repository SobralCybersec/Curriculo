# Progress

## Current goal

- Detect XeLaTeX before compilation and install only missing template packages where the installed distribution supports it.

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
- `Java/src/main/java/com/dev/view/ResumeEditorView.java`
- `Java/src/main/java/com/dev/view/PDFPreviewPanel.java`
- `Java/src/main/java/com/dev/view/OptionsPanel.java`
- `Java/src/main/java/com/dev/view/CreditsPanel.java`
- `Java/src/test/java/com/dev/service/ResumeServiceTest.java`
- `Java/src/test/java/com/dev/service/LatexEnvironmentTest.java`
- `Java/src/test/java/com/dev/BuildInfoTest.java`
- `Java/src/test/java/com/dev/util/LatexParserTest.java`
- `Java/src/test/java/com/dev/util/LatexGeneratorTest.java`
- `Java/src/test/java/com/dev/util/LatexPropertyTest.java`
- `Java/src/test/java/com/dev/util/UIThemeTest.java`
- `Java/src/test/java/com/dev/view/OptionsPanelTest.java`
- `Java/src/test/java/com/dev/view/PDFPreviewPanelTest.java`
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

## Remaining work

- [x] Move PDF rendering off the EDT and add asynchronous preview regression coverage.
- [x] Provide a Linux/Wayland launcher and attach it to releases.
- [x] Generate unique commit-versioned CI artifacts and tag-versioned release artifacts without CI commits.
- [x] Add bounded XeLaTeX detection, compile logs/timeouts, and MiKTeX on-demand package installation.
- [x] Prompt for the official first-run XeLaTeX installer when the compiler is absent; keep checks on every compile.
- [ ] Future: make multi-file writes atomic.
- [ ] Environment: repair the local XeLaTeX format (`xelatex.fmt`) before end-to-end compile profiling.
