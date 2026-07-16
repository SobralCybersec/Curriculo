# Progress

## Current goal

- Expand regression coverage and remove confirmed UI performance hotspots without rewriting the application.

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

## Decisions made

- Keep Java/Swing and the existing LaTeX-on-disk contract.
- Do not alter existing uncommitted UI, dependency, documentation, or root `src/` work.
- Remove unused `ResumeData` and Gson instead of introducing an unused model layer.
- Snapshot Swing state on EDT; run file writes, XeLaTeX, and PDF metadata work in a `SwingWorker`.
- Avoid repeated first-page renders and skip 200 DPI preview rendering when magnifier is disabled.

## Verified checks

- [x] `rtk mvn test` from `Java/`: 19 tests pass.
- [x] `rtk mvn package` from `Java/`: package succeeds.
- [x] `rtk git diff --check`: no whitespace errors.

## Remaining work

- [x] Fix dynamic saved-file loading and add regression test.
- [x] Fix nested-brace and summary parsing; add round-trip tests.
- [x] Stop compile/export path after save failure; quote regex replacements.
- [x] Run test/package, inspect diff, update this file.
- [x] Move save/compile/metadata work off EDT; prevent concurrent compile actions.
- [x] Remove duplicate initial preview rendering; make 200 DPI rendering magnifier-dependent.
- [ ] Future: move PDF page rendering off EDT; make multi-file writes atomic; preserve compiler diagnostics and add a compiler timeout.
