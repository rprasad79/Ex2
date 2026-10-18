package assignment2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Per-method symbol table.  Frame layout (after JSR + LINK, FBR -> saved FBR):
 *   FBR-1            return address
 *   FBR-1-n+i        formal parameter i (0-based) of n   => offset i-n-1
 *   FBR-2-n          return-value slot                    => offset -n-2
 *   FBR+1+j          local variable j                     => offset j+1
 */
class SymbolTable {
    static final class Var {
        final String name; final Type type; int offset;
        Var(String name, Type type, int offset) { this.name = name; this.type = type; this.offset = offset; }
    }

    private final Map<String, Var> vars = new HashMap<>();
    private final List<Var> params = new ArrayList<>();
    private int numLocals = 0;

    void declareParam(String name, Type t) {
        if (vars.containsKey(name)) throw new CompileError("duplicate parameter '" + name + "'");
        Var v = new Var(name, t, 0);
        vars.put(name, v);
        params.add(v);
    }

    /** Call once all formals are known so offsets can be assigned. */
    void finishParams() {
        int n = params.size();
        for (int i = 0; i < n; i++) params.get(i).offset = i - n - 1;
    }

    void declareLocal(String name, Type t) {
        if (vars.containsKey(name)) throw new CompileError("variable '" + name + "' is already declared in this method");
        vars.put(name, new Var(name, t, 1 + numLocals++));
    }

    Var lookup(String name) { return vars.get(name); }
    int numLocals() { return numLocals; }
    int numParams() { return params.size(); }
    int returnSlotOffset() { return -params.size() - 2; }
}
