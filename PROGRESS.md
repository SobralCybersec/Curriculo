# Progress

## Current goal

- Improve Swing responsiveness on Linux/Wayland while keeping the existing Java 21 desktop distribution model.

## Files touched

- `PROGRESS.md`
- `Java/pom.xml`
- `Java/src/main/java/com/dev/Main.java`
- `Java/src/main/java/com/dev/model/ResumeData.java` (deleted)
- `Java/src/main/java/com/dev/service/ResumeService.java`
- `Java/src/main/java/com/dev/util/LatexParser.java`
- `Java/src/main/java/com/dev/view/ResumeEditorView.java`
- `Java/src/main/java/com/dev/view/PDFPreviewPanel.java`
- `Java/src/main/java/com/dev/view/OptionsPanel.java`
- `Java/src/test/java/com/dev/service/ResumeServiceTest.java`
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

## Verified checks

- [x] `rtk mvn --batch-mode --no-transfer-progress verify` from `Java/`: 30 tests passed after worker rendering.
- [x] JFR test profile command completed and `target/tests.jfr` has a valid summary.
- [x] Wayland-session smoke: timed six-second JAR run stayed alive until the expected timeout and wrote a valid JFR; no application error was emitted.
- [x] `rtk sh -n scripts/curriculo-editor-linux`: launcher syntax valid.
- [x] Ruby parsed all GitHub workflow YAML files.
- [x] `rtk git diff --check`: no whitespace errors.
- [x] Earlier performance baseline: 72 DPI A4 preview allocated 1.9 MB; the optional 200 DPI preview added 14.7 MB. Magnifier-off avoids that extra allocation.

## Remaining work

- [x] Move PDF rendering off the EDT and add asynchronous preview regression coverage.
- [x] Provide a Linux/Wayland launcher and attach it to releases.
- [ ] Future: make multi-file writes atomic; preserve compiler diagnostics and add a compiler timeout.
- [ ] Environment: repair XeLaTeX format (`xelatex.fmt`) before end-to-end compile profiling.
