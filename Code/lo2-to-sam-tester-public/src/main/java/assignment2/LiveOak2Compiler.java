package assignment2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import edu.utexas.cs.sam.io.SamTokenizer;
import edu.utexas.cs.sam.io.Tokenizer.TokenType;

public class LiveOak2Compiler {

	// Helper class for variable metadata in Symbol Table
	static class Symbol {
		String name;
		String type; // "int", "bool", "String"
		int offset; // Relative offset from FBR

		Symbol(String name, String type, int offset) {
			this.name = name;
			this.type = type;
			this.offset = offset;
		}
	}

	// Symbol Table for a specific method scope
	static class SymbolTable {
		private final Map<String, Symbol> table = new HashMap<>();

		public void add(String name, String type, int offset) {
			table.put(name, new Symbol(name, type, offset));
		}

		public Symbol get(String name) {
			return table.get(name);
		}

		public boolean contains(String name) {
			return table.containsKey(name);
		}
	}

	// Helper class to manage nested loops for 'break' statement resolution
	static class LoopContext {
		String startLabel;
		String exitLabel;

		LoopContext(String startLabel, String exitLabel) {
			this.startLabel = startLabel;
			this.exitLabel = exitLabel;
		}
	}

	private final SamTokenizer tokenizer;
	private final List<String> code = new ArrayList<>();
	private int labelCounter = 0;
	private final Stack<LoopContext> loopStack = new Stack<>();

	public LiveOak2Compiler(String inputFile) throws IOException {
		this.tokenizer = new SamTokenizer(inputFile, SamTokenizer.TokenizerOptions.PROCESS_STRINGS);
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			throw new Error("Expected 2 arguments: source file and output SaM file");
		}
		String inputFile = args[0];
		String outputFile = args[1];

		// TODO: call compiler, write SaM code to file...

	}

	static String compiler(String fileName) {
		// returns SaM code for program in file
		try {
			SamTokenizer f = new SamTokenizer(fileName, SamTokenizer.TokenizerOptions.PROCESS_STRINGS);
			String pgm = getProgram(f);
			return pgm;
		} catch (Exception | Error e) {
			// Note: if running on Windows for local grader, may need to modify filename
			// with something like this:
			// fileName.replace('\\', '/');

			// The grader expects this message on compilation failure.
			System.err.println("Failed to compile " + fileName);
			throw new Error(e);
		}
	}

	static String getProgram(SamTokenizer f) {
		// TODO: compile the complete program.
		// When detecting a syntax or other compilation error anywhere in the compiler,
		// throw an Error with a useful message indicating what went wrong.

		String pgm = "";
		while (f.peekAtKind() != TokenType.EOF) {
			pgm += getMethod(f);
		}
		return pgm;

	}

	static String getMethod(SamTokenizer f) {
		// TODO: add code to convert a method declaration to SaM code.
		f.check("int"); // must match at begining
		String methodName = f.getWord();
		f.check("("); // must be an opening parenthesis
		String formals = getFormals(f);
		f.check(")"); // must be an closing parenthesis
		// You would need to read in formals if any
		// And then have calls to getDeclarations and getStatements.
		return null;
	}

	static String getExp(SamTokenizer f) {
		switch (f.peekAtKind()) {
			case INTEGER: // E -> integer
				return "PUSHIMM " + f.getInt() + "\n";
			case OPERATOR: {
			}
			default:
				// Simple example: provide a more informative error message when possible
				throw new Error("Invalid expression.");
		}
	}

	static String getFormals(SamTokenizer f) {
		return null;
	}
}
