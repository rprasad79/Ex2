package assignment2;

/** Any syntactic or semantic error detected in the LiveOak input program. */
class CompileError extends RuntimeException {
    private static final long serialVersionUID = 1L;
    CompileError(String msg) { super(msg); }
}
