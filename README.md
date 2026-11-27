# openflexo-xlsx — Excel technology adapter

**Status:** Active <!-- Active · Frozen · POC · Deprecated · Retired: same as TA-CATALOG.md -->

Reads and writes Excel workbooks: sheets, rows, columns and cells, plus a reflected model slot, configured with a VirtualModel, that reflects the workbook as concept instances.

| | |
|---|---|
| Technology | Excel workbooks (.xls, .xlsx), through Apache POI |
| Libraries | `poi-ooxml` 5.5.1, `poi` 5.5.1 |
| Family | `DOC` · `R/W+GEN` (see the classification of `openflexo-technology-adapters/TA-CATALOG.md`) |
| Resource factories | `ExcelWorkbookResourceFactory` (.xls, .xlsx) |
| Test resource center | `http://www.openflexo.org/excel-test` |

## FML surface

| Model slot | FML name | Roles | Edition actions and behaviours | Fetch requests |
|---|---|---|---|---|
| `BasicExcelModelSlot` | `BasicExcelModelSlot` | `ExcelSheetRole`, `ExcelColumnRole`, `ExcelRowRole`, `ExcelCellRole` | `CreateExcelResource`, `GenerateExcelResource`, `AddExcelCell`, `AddExcelRow`, `AddExcelSheet`, `CellStyleAction`, `MergeCells` | `SelectExcelCell`, `SelectExcelRow`, `SelectExcelSheet`, `SelectUniqueExcelCell`, `SelectUniqueExcelRow`, `SelectUniqueExcelSheet` |
| `FMLExcelModelSlot` | `FMLExcelModelSlot` | — | — | — |

## Example

From the test resource center (`TestResourceCenter/FML/ReflectPersonListing.fml/ReflectPersonListing.fml`):

```fml
use org.openflexo.technologyadapter.excel.FMLExcelModelSlot as XLS;

import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;

import ["http://www.openflexo.org/excel-test/FML/PersonListing.fml"];

@URI("http://www.openflexo.org/excel-test/FML/ReflectPersonListing.fml")
public model ReflectPersonListing {
   PersonListing personListing with FMLExcelModelSlot();

   create(required Resource<ExcelWorkbook> aListing) {
      connect personListing using aListing ;
   }
}
```

Scripts: `xlsx-ta-test-rc/…/TestResourceCenter/AutomatedTests/`, `xlsx-ta-test-rc/…/TestResourceCenter/ExcelErrorKinds/`.

## Modules

| Module | Content |
|---|---|
| `xlsx-ta` | adapter, model slots, resources, domain model, dictionaries |
| `xlsx-ta-ui` | controller, icons, module views, inspectors |
| `xlsx-ta-test` | tests |
| `xlsx-ta-test-rc` | test resource center |

## Build and test

From `openflexo-dev` (local sources of the whole platform):

    ./gradlew --continue :openflexo-xlsx:xlsx-ta-test:cleanTest :openflexo-xlsx:xlsx-ta-test:test

Tests run with `ignoreFailures = true`: read the `Results:` line, not the exit status.

## Known defects and backlog

- Known defects: [KNOWN_DEFECTS.md](KNOWN_DEFECTS.md) (`XLSX-D-<n>`), fixed ones in `FIXED_DEFECTS.md` (created with the first fixed defect).
- Features, evolutions and user requests: [BACKLOG.md](BACKLOG.md) (`XLSX-F-<n>`).
- Issues: https://github.com/openflexo-team/openflexo-xlsx/issues

## License

Dual-licensed under the EUPL (1.1 or later) and the GNU GPL (3 or later), as stated in the header of each source file;
[LICENSE](LICENSE) holds the text of the GPL.
