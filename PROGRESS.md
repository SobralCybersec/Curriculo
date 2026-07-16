# Progress

## Current goal

- Add deterministic code-quality and performance-profile CI gates.

## Files touched

- `PROGRESS.md`
- `Java/pom.xml`
- `Java/src/main/java/com/dev/model/ResumeData.java` (deleted)
- `Java/src/main/java/com/dev/service/ResumeService.java`
- `Java/src/main/java/com/dev/util/LatexParser.java`
- `Java/src/main/java/com/dev/view/ResumeEditorView.java`
- `Java/src/main/java/com/dev/view/PDFPreviewPanel.java`
- `Java/src/main/java/com/dev/view/OptionsPanel.java`
- `Java/src/test/java/com/dev/service/ResumeServiceTest.java`
- `Java/src/test/java/com/dev/util/LatexParserTest.java`
- `Java/src/test/java/com/dev/util/LatexGeneratorTest.java`
- `Java/src/test/java/com/dev/util/UIThemeTest.java`
- `Java/src/test/java/com/dev/view/OptionsPanelTest.java`
- `Java/src/test/java/com/dev/view/PDFPreviewPanelTest.java`
- `.github/workflows/ci.yml`
- `.github/workflows/release.yml`
- `.github/workflows/code-quality.yml`
- `.github/workflows/performance.yml`
- `Java/src/test/java/com/dev/util/LatexPropertyTest.java`

## Decisions made

- Keep Java/Swing and the existing LaTeX-on-disk contract.
- Do not alter existing uncommitted UI, dependency, documentation, or root `src/` work.
- Remove unused `ResumeData` and Gson instead of introducing an unused model layer.
- Snapshot Swing state on EDT; run file writes, XeLaTeX, and PDF metadata work in a `SwingWorker`.
- Avoid repeated first-page renders and skip 200 DPI preview rendering when magnifier is disabled.
- Default the magnifier off: JFR measured A4 200 DPI raster allocation at 14.7 MB in addition to 1.9 MB at 72 DPI.
- Flush replaced preview images and clear document/images on load/render errors and window close.
- CI uses Java 21, Maven dependency caching, `verify`, and a downloadable shaded JAR artifact.
- Release runs only on `v*` tags, attaches versioned JAR plus SHA-256, and has only `contents: write` permission.
- Maven Enforcer now requires Maven 3.9+, JDK 21, and converged dependencies.
- Code quality CI runs strict compile-scope dependency analysis; weekly/manual performance CI archives JFR instead of using flaky runner-time thresholds.

## Verified checks

- [x] JFR baseline `/tmp/cv-preview-before.jfr`: A4 preview allocated 1.9 MB (72 DPI) + 14.7 MB (200 DPI).
- [x] JFR after `/tmp/cv-preview-after.jfr`: magnifier-off A4 preview allocated 1.9 MB only.
- [x] `rtk mvn test` from `Java/`: 21 tests pass.
- [x] `rtk mvn package` from `Java/`: package succeeds.
- [x] `rtk mvn --batch-mode --no-transfer-progress verify` from `Java/`: 30 tests pass.
- [x] Strict dependency analysis: no dependency problems.
- [x] Performance workflow command produced `Java/target/tests.jfr` and a valid JFR summary.
- [x] Ruby parsed both GitHub workflow YAML files.
- [x] `rtk git diff --check`: no whitespace errors.

## Remaining work

- [x] Fix dynamic saved-file loading and add regression test.
- [x] Fix nested-brace and summary parsing; add round-trip tests.
- [x] Stop compile/export path after save failure; quote regex replacements.
- [x] Run test/package, inspect diff, update this file.
- [x] Move save/compile/metadata work off EDT; prevent concurrent compile actions.
- [x] Remove duplicate initial preview rendering; make 200 DPI rendering magnifier-dependent.
- [x] Profile and reduce A4 preview allocation; cover clear and invalid-PDF recovery.
- [x] Add CI and tag-based release workflows; expand regression suite to 28 tests.
- [x] Add Enforcer, dependency-quality CI, jqwik property coverage, and scheduled/manual JFR profiling.
- [ ] Future: move PDF page rendering off EDT; make multi-file writes atomic; preserve compiler diagnostics and add a compiler timeout.
- [ ] Environment: repair XeLaTeX format (`xelatex.fmt`) before end-to-end compile profiling.
