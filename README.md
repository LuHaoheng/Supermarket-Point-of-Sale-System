# ProjectPos_2026 — Java Swing Supermarket POS

A dependency-free Java 17+ desktop system for **Process Sale** and **Handle Returns**, created for *Software Engineering Practice Assignment*. Works on Windows, macOS, and Linux with Java 17 or newer. No Maven download, database server, GitHub, or network is needed.

## Open and run in IntelliJ IDEA

1. Extract this entire ZIP to a writable folder. In **IntelliJ IDEA → File → Open**, choose the extracted `ProjectPos_2026` folder.
2. Configure **Project SDK: JDK 17 or newer** (File → Project Structure → Project). IntelliJ should detect the `.idea` project configuration.
3. Open `src/pos/Main.java`, click the green Run triangle beside `main`, and choose **Run 'Main.main()'**.
4. If IDEA asks to add source roots, mark `src` as **Sources Root** and `test` as **Test Sources Root**. Do not run from inside the ZIP.
5. The system generates `data/pos-data.bin` on the **first successful transaction or inventory update**. Demo products appear automatically on a new install. The app stores data under the working directory (configured as `$PROJECT_DIR$`). To reset the demo, close the program and delete `data/pos-data.bin`.

## User demonstration

**Sale**: On `1 Process Sale`, select a product, quantity, click **Add selected item**, enter cash (>= total), click **Complete cash sale**, and optionally save the receipt as `.txt`. The sale ID appears on the receipt. Stock decreases.

**Return**: On `2 Handle Returns`, select the sale and click **Use selected sale**; type a purchased SKU (e.g. `1001`) and return quantity; click **Process return & refund**. Refund uses the **original sale price**. Stock increases. A SKU cannot be returned beyond its sold quantity, even after re-opening the app.

**Inventory**: On `3 Inventory`, add products or restock existing SKUs. Data persists on restart.

## Run regression tests (terminal, Java 17+)

From the project folder:

macOS/Linux: `bash run-tests.sh`

Windows: `run-tests.bat`

Tests use a temporary data file and do not alter demo data. The script compiles only Java standard-library source. It reports every check and exits nonzero on failure.

## Implementation notes

- Java Swing interface; Model / Service / Store / UI layers.
- `BigDecimal` monetary calculations (no floating-point rounding); all mutations validated before commit.
- Persistence via Java `ObjectOutputStream`, with temporary-file replacement. No external dependencies. This local file format is for a coursework prototype, not a production multi-user database.
- Refunds require a recorded sale and respect the already-returned quantity.
- The system supports **cash only**, no barcode scanner, tax, payment gateway, authentication, or multiple concurrent terminals.
- Original assignment mentions collaboration, remote GitHub repository, and video URL; **these are intentionally not included**, per the user's explicit request. Report and PPT include the academic planning and OO design artifacts.
