# openflexo-xlsx — Backlog

Features and evolutions to develop in the Excel technology adapter. Each entry says what problem it solves and when it is done.
Bugs that are known and not fixed go to `KNOWN_DEFECTS.md`, not here.

Identifiers are stable and never reused: `XLSX-F-<n>`. Reference them from commits and from other entries.

Status legend: `TODO` · `IN PROGRESS` · `BLOCKED` · `DONE` · `DEFERRED`.

---

## FML expressiveness

### XLSX-F-1 — Finish exposing the Excel edition actions to textual FML  ·  `TODO`

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
