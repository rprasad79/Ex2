# LiveOak-2 -> SaM compiler (assignment2.LiveOak2Compiler)

    ./build.sh /path/to/SaM-2.6.3.jar          # creates compiler.jar + source.zip
    java -jar compiler.jar test1.lo output.sam
    java -cp SaM-2.6.3.jar edu.utexas.cs.sam.ui.SamText output.sam

If your jar uses the older package `edu.cornell.cs.sam.io`, change the three imports at the top of
`LiveOak2Compiler.java`.

## Design
* Pass 1 scans method headers (skipping bodies) so methods can be called before they are defined.
* Pass 2 is a recursive-descent parser (one function per grammar rule) that type-checks and emits SaM code directly.
* Each method has its own SymbolTable (formals + locals).
* Calling convention: caller `ADDSP 1` (return slot), pushes args, `JSR M_name`, `ADDSP -n`.
  Callee `LINK`, `ADDSP #locals`; `return` = `STOREOFF -(n+2)`, `ADDSP -#locals`, `UNLINK`, `RST`.
  Params at FBR+(i-n-1), locals at FBR+1+j.
* `&` / `|` short-circuit; `?:` evaluates one branch; strings are null-terminated heap arrays and `+ * ~ < > =`
  on strings call small SaM runtime routines (emitted only when used).
* Errors (syntax, types, undeclared names, bad calls, `break` outside loop, missing main ...) print a message to
  stderr, delete the output file and exit 1.

## Tests
`tests/run_tests.sh` uses a tiny SaM emulator (`tests/sam_emu.py`) for local checking; always re-verify with the real SamText.
