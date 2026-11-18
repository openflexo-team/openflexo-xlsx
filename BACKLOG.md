# openflexo-xlsx — Backlog

Features and evolutions to develop in the Excel technology adapter. Each entry says what problem it solves and when it is done.
Bugs that are known and not fixed go to `KNOWN_DEFECTS.md`, not here.

Identifiers are stable and never reused: `XLSX-F-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## FML expressiveness

### XLSX-F-1 — Finish exposing the Excel edition actions to textual FML  ·  `TODO`

**GitHub:** [openflexo-team/openflexo-xlsx#3](https://github.com/openflexo-team/openflexo-xlsx/issues/3)

**Problem.** An edition action is writable in textual FML only through the properties annotated `@FMLAttribute`; every other property
is rejected with `Unknown property <name> for <Action>` and silently lost on a round trip. Only three actions are annotated today —
`AddExcelRow`, `AddExcelCell` and `AddExcelSheet`, annotated while migrating formod (2026-09-17) because its goal model serialization
needs them.

**To do.** Annotate the remaining actions of `org.openflexo.technologyadapter.excel.fml.editionaction`: `MergeCells`,
`CellStyleAction`, `CreateExcelResource`, `GenerateExcelResource`, and the fetch requests `SelectExcelCell`, `SelectExcelRow`,
`SelectExcelSheet`, `SelectUniqueExcelCell`, `SelectUniqueExcelRow`, `SelectUniqueExcelSheet`. Enum-valued properties take
`kind = AttributeKind.Enum` (as `AddExcelCell.cellType` now does) and are written `$Value` in FML.

**Acceptance criteria.** A `.fml` file setting every documented property of each action parses, validates with no error, and
pretty-prints back to the same text; a `.fmlscript` in `xlsx-ta-test` exercises the actions it can run headless.

**Note.** Two actions carried the wrong FML name, fixed at the same time: `MergeCells` was published as `AddExcelCell` (so that name
resolved to whichever of the two came first, and `AddExcelCell` calls were validated against `MergeCells`), and
`GenerateExcelResource` was published as `GenerateDocXDocument`.

## FML runtime errors

### XLSX-F-2 — The Excel adapter raises typed FML errors instead of returning null after a warning or a printed stack  ·  `DONE`

**GitHub:** [openflexo-team/openflexo-xlsx#5](https://github.com/openflexo-team/openflexo-xlsx/issues/5)

**Problem.** Part of `CORE-F-11` phase 6 (`openflexo-core/BACKLOG.md`): the edition actions of the Excel adapter caught the failures of
their evaluations four at a time, printed the stack and went on; a missing sheet, row or name gave a warning and a null result; some
declared no `throws FMLExecutionException`. `GenerateExcelResource` built its exceptions without throwing them. A select from a null sheet
dereferenced it. `XLSVirtualModelInstance.createReflectedInstance` returned null on an unknown concept or a missing sheet, and a reflected
cell of the wrong type failed inside POI.

**Done (decided with the user, 2026-11-16).** Scope: the paths that FML execution goes through.
- Edition actions (`AddExcelCell`, `AddExcelRow`, `AddExcelSheet`, `MergeCells`, `CellStyleAction`, `CreateExcelResource`,
  `GenerateExcelResource`) declare `throws FMLExecutionException` and evaluate through `FMLEvaluation`. A null sheet, row, workbook, index,
  name, cell or style subject is an `FMLNullReferenceError`; a row index with no row, a merge refused by POI, a missing Excel model slot, a
  failing resource creation an `FMLTechnologyError`; a `ModelDefinitionException` an `FMLInternalError`; a cancelled loading an
  `FMLCancellation`. A typed failure of `AbstractCreateResource` is rethrown unchanged. A null cell value leaves the cell empty (no warning).
- Fetch requests (`SelectExcelSheet`, `SelectExcelRow`, `SelectExcelCell` and their unique forms): a sheet given but null, or a null
  workbook, is an `FMLNullReferenceError` naming the select; a sheet not given still selects from every sheet.
- `createReflectedInstance` declares `throws FMLExecutionException`: an unknown concept is an `FMLResolutionError`; no virtual model, no
  `@DataRange` sheet, no workbook, a missing sheet an `FMLTechnologyError`.
- A reflected cell read while FML runs whose content does not fit its property (text in a numeric column) is an `FMLTypeError` naming the
  cell, through the new strict entry point `FlexoConceptInstance.getFlexoActorOrFail` of the core; the user interface still reads null, with a
  warning. A missing date cell gives null (it fell through to the boolean case).
- The factory of reflected instances (`XLSVirtualModelInstanceModelFactory`) declares no exception: it runs no creation scheme and cannot
  fail, so the silent catches of `XLSVirtualModelInstance` and `XLSVirtualModelInstanceBuilder` are gone (`CORE-D-73`: nothing to discard
  there).
- The `System.out` traces of the actions are removed.
- After review (decided with the user): `AddExcelCell` at a row index with no row is an `FMLTechnologyError` (create the row with
  `AddExcelRow` first); `GenerateExcelResource` without its Excel model slot an `FMLTechnologyError`; `SelectExcelCell` without a sheet
  selects every cell of the workbook (it threw a `NullPointerException`), as `SelectExcelRow`. The lenient reading of a cell of the wrong
  type logs at FINE (a label is read again and again).

**Left as they are (scope).** The `printStackTrace` / `System.out` of loading, conversion and model slot code: about 60 lines in
`FMLExcelModelSlot`, `BasicExcelModelSlot`, `model/`, `rm/`, `fml/reflect/rm`, `ExcelActorReference`.

**Not done.** `new` of a reflected concept in FML gives null after a warning ("Not implemented feature",
`AbstractCreationSchemeAction`): recorded in `CORE-D-73` (decided with the user). `XLSX-D-2`: tried to reproduce only (decided with the
user), see its entry.

**Tests.** `TestExcelErrorKinds` (xlsx-ta-test, scripts of `TestResourceCenter/ExcelErrorKinds`, fixtures `ExcelErrorKinds.fml` and
`ReflectErrorKinds.fml`): a row added to a null sheet, a sheet without a name, `createReflectedInstance` of an unknown concept, a cell of the
wrong type; each asserts the type of the error and the behaviour frame and line, and that nothing is printed to stderr.

**Verified (2026-11-16).** Green through the openflexo-dev composite: xlsx-ta-test 42, flexo-foundation-test 735, fml-cli-test 153,
openflexo-integration-tests (integration-tests, digital-twin-demo, city-mapping, multi-process-challenge), formod (b-ta-test,
formod-test), xml-ta-test, jdbc-ta-test. ECJ compiles flexo-foundation and xlsx-ta. Reviewed by `openflexo-reviewer` (its findings fixed or
recorded here and in `CORE-D-84`). Not checked in the running application (a reflected sheet with a cell of the wrong type, the
digital-twin manipulation with a missing sheet).
