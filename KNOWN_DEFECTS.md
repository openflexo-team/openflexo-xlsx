# openflexo-xlsx — Known defects

Bugs of openflexo-xlsx that are known and not fixed yet. Each entry says what is verified, how to reproduce it, and what is fixed or
not. Features and evolutions go to `BACKLOG.md`, not here.

Identifiers are stable and never reused: `XLSX-D-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## User-interface tests

### XLSX-D-1 — `TestFIBCellRangeSelector` fails with a `NullPointerException` when a display is available  ·  `TODO`

**GitHub:** [openflexo-team/openflexo-xlsx#1](https://github.com/openflexo-team/openflexo-xlsx/issues/1)

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

## Reflected model slot

### XLSX-D-2 — After an external edit and a refresh, the reflected model slot is rebuilt over a stale sheet: `NullPointerException`  ·  `TODO`

**GitHub:** [openflexo-team/openflexo-xlsx#2](https://github.com/openflexo-team/openflexo-xlsx/issues/2)

**Symptom (verified 2026-11-15 by execution).** In `openflexo-integration-tests/digital-twin-demo`, `ExternalWorkbookEditTest.externalEditIsPickedUpByRefresh`
fails: a `select` whose container is a property of the reflected Excel model slot rebuilds the reflected virtual model instance
(`ReflectedFMLRTModelSlotInstance.getAccessedResourceData` → `FMLExcelModelSlot.reflectVirtualModelInstance` →
`XLSVirtualModelInstanceBuilder.buildVirtualModelInstance` → `DataRange.isSignificative` → `ExcelSheet.getRowAt` → `getExcelRows` →
`ensureConversion`), and `BasicExcelModelConverter.getSheetReference` throws a `NullPointerException`: `excelWorkbook` is null, the converter
having been deleted (`BasicExcelModelConverter.delete()`). So the sheet traversed belongs to the workbook released by the refresh.

**What is verified.** Before `CORE-F-11` phase 3 the failure was swallowed: the container of the `select` printed the stack and gave null, and
the test passed. It now reaches the test as an `FMLInternalError` (a defect of the platform). Not investigated: why the rebuild after the
refresh still reaches the sheets of the former workbook (the data ranges of the reflected slot keeping their sheets, or the reflected instance
not dropped by the refresh; see the hot reload of the reflected slot, `refresh()` versus `synchronize()`).

**Not reproduced since.** In the final run of `CORE-F-11` phase 3 (whole suite) and in two runs of the test alone, the test passes: the
failure was seen once, in a run of the whole suite before the last changes of phase 3 (none of which touches this path knowingly). Possibly
order- or timing-dependent; the stack above is the evidence.

**Runs during `XLSX-F-2` (2026-11-16).** Green in the whole digital-twin-demo suite and in three runs of the test alone, after the
changes of `XLSX-F-2`. Still not reproduced.

**To reproduce (not reproduced since).** From `openflexo-dev`:
`./gradlew :openflexo-integration-tests:digital-twin-demo:cleanTest :openflexo-integration-tests:digital-twin-demo:test --tests '*ExternalWorkbookEditTest'`.
