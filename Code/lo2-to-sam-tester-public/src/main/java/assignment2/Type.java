package assignment2;

/** The three LiveOak-2 value types. */
enum Type {
    INT("int"), BOOL("bool"), STRING("String");

    final String name;

    Type(String name) { this.name = name; }

    /** @return the Type named by the keyword, or null if it is not a type keyword. */
    static Type fromKeyword(String w) {
        for (Type t : values()) if (t.name.equals(w)) return t;
        return null;
    }

    @Override public String toString() { return name; }
}
