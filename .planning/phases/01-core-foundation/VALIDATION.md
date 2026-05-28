# Phase 1 Validation: Core Foundation & UI Theme

## Goal
Establish a centralized theme system and clean up legacy UI code to ensure visual consistency and maintainability.

## Validation Truths (Observable Behaviors)

### T-1.1 Centralized Styling
- **Truth**: All UI components use colors defined in `UITheme.java`.
- **Verification**: `grep` check across `src/main/java/com/dev/view` for hardcoded `new Color(...)` patterns.
- **Success Criteria**: Zero matches for hex or RGB literals in view classes.

### T-1.2 Dead Code Purge
- **Truth**: All identified unused components and methods are removed.
- **Verification**: Check for absence of `AnimatedMenu.java` and unreferenced methods in `AnimationUtil.java`.
- **Success Criteria**: File `AnimatedMenu.java` does not exist; `mvn compile` passes.

### T-1.3 Network Independence
- **Truth**: `BannerPanel` no longer attempts network requests or image loading.
- **Verification**: Inspect `BannerPanel.java` for `java.net.*` or `javax.imageio.*` imports and logic.
- **Success Criteria**: No network/image logic remains in the UI layer.

### T-1.4 Comment-Free Codebase
- **Truth**: Refactored files contain zero comments (enforcing the Clean Code constraint).
- **Verification**: `grep -c "//"` on touched files.
- **Success Criteria**: Result is `0` for all refactored files.

## Acceptance Tests

| ID | Test Description | Command / Action | Expected Result |
|----|------------------|------------------|-----------------|
| AT-01 | Full Project Build | `mvn clean compile` | Build Success |
| AT-02 | Property-Based Tests | `mvn test -Dtest=UIThemeTest` | All tests pass |
| AT-03 | Comment Audit | `grep -r "//" Java/src/main/java/com/dev` | No comments in refactored files (excluding URLs) |
| AT-04 | Dependency Audit | `grep "radiance-animation" Java/pom.xml` | No matches found |
| AT-05 | Visual Inspection | Run application | Consistent dark theme, no visual regressions |

## Validation Architecture (Verification Loop)
1. **Developer Run**: Execute `mvn clean compile` after every task.
2. **Automated Gate**: Pre-commit validation via `mvn test`.
3. **Audit Gate**: Run `grep` commands defined in AT-03 and AT-04 before finalizing Phase 1.
4. **Human Review**: Launch the application to verify visual fidelity and performance.
