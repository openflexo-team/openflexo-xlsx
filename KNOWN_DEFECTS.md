# openflexo-xlsx — Known defects

Bugs of openflexo-xlsx that are known and not fixed yet. Each entry says what is verified, how to reproduce it, and what is fixed or
not. Features and evolutions go to `BACKLOG.md`, not here.

Identifiers are stable and never reused: `XLSX-D-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## User-interface tests

### XLSX-D-1 — `TestFIBCellRangeSelector` fails with a `NullPointerException` when a display is available  ·  `TODO`

**Symptom.** In `xlsx-ta-ui`, the three tests of `TestFIBCellRangeSelector` (`testSelectorWithNoRange`, `testSelectorWithRangeOnSheet1`,
`testSelectorWithRangeOnSheet2`) fail with a `NullPointerException` at their first instruction (lines 72, 90 and 113):
`getWorkbook("PersonListing.xlsx")`. The workbook is not found in the UI test environment.

**What is verified.** Seen on the Jenkins job `openflexo-xlsx-release` build #1 (2026-10-16), whose release job had an Xvnc display.
Before that, the daily job `openflexo-xslx-2.99` had no display and the same class failed in `classMethod` with `HeadlessException`
(3 tests), the build staying blue because the build plugin ignores test failures. Not reproduced locally, not investigated: whether the
code, the test resource or the base class `AbstractTestExcelUI` is at fault is unknown.

**To reproduce.** With a display, from the `openflexo-dev` composite:
`./gradlew :openflexo-xlsx:xlsx-ta-ui:cleanUiTest :openflexo-xlsx:xlsx-ta-ui:uiTest --tests '*TestFIBCellRangeSelector'`.

**Consequence for the 2.99 release.** The release job `openflexo-xlsx-release` runs the `test` task, so the UI tests of this module were not
executed for the release (tracked as `JENKINS-D-6` in the Jenkins backlog).
