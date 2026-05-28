# Roadmap: CV Generator Refactoring & Packaging

## Phase 1: Core Foundation & UI Theme (Current)
**Goal:** Establish a centralized theme system and clean up legacy UI code to ensure visual consistency and maintainability.
**Plans:** 3 plans
- [ ] 01-01-PLAN.md — Foundation & UITheme Implementation
- [ ] 01-02-PLAN.md — Component Refactoring
- [ ] 01-03-PLAN.md — Main View Refactoring & Cleanup

## Phase 2: LaTeX Pipeline Refinement
- [ ] 2.1 Simplify `LatexGenerator` and implement helper methods.
- [ ] 2.2 Fix `LatexParser` correctness (nested braces, independent section parsing).
- [ ] 2.3 Add property-based tests for Generator/Parser round-trips.

## Phase 3: Cleanup & Optimization
- [ ] 3.1 Remove unused animation methods and `radiance-animation` dependency.
- [ ] 3.2 Simplify `BannerPanel` and remove unused assets/logic.
- [ ] 3.3 Delete `AnimatedMenu` and other dead UI components.
- [ ] 3.4 Improve naming and readability across the codebase.

## Phase 4: Packaging & Distribution
- [ ] 4.1 Optimize `pom.xml` and build process.
- [ ] 4.2 Implement `jpackage` build script for Windows.
- [ ] 4.3 Verify self-contained executable behavior and error handling.
