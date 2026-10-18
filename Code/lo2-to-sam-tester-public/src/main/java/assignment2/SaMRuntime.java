package assignment2;

/**
 * SaM assembly for the string runtime routines used by LiveOak string operators.
 *
 * Convention for these helpers ("in place"): the caller pushes the n arguments
 * left to right and does JSR; the helper writes its result over the FIRST
 * argument's slot, and the caller then does ADDSP -(n-1), leaving the result on
 * top of the stack.  Strings are null-terminated sequences of cells on the heap.
 */
final class SaMRuntime {
    private SaMRuntime() {}

    static final String STRLEN = String.join("\n",
        "RT_strlen:", "LINK", "ADDSP 1", "PUSHIMM 0", "STOREOFF 1",
        "RTSL_top:", "PUSHOFF -2", "PUSHOFF 1", "ADD", "PUSHIND", "ISNIL", "JUMPC RTSL_end",
        "PUSHOFF 1", "PUSHIMM 1", "ADD", "STOREOFF 1", "JUMP RTSL_top",
        "RTSL_end:", "PUSHOFF 1", "STOREOFF -2", "ADDSP -1", "UNLINK", "RST", "");

    // concat(a,b): a at -3, b at -2.  locals: 1=len a, 2=len b, 3=result, 4=i
    static final String CONCAT = String.join("\n",
        "RT_concat:", "LINK", "ADDSP 4",
        "PUSHOFF -3", "JSR RT_strlen", "STOREOFF 1",
        "PUSHOFF -2", "JSR RT_strlen", "STOREOFF 2",
        "PUSHOFF 1", "PUSHOFF 2", "ADD", "PUSHIMM 1", "ADD", "MALLOC", "STOREOFF 3",
        "PUSHIMM 0", "STOREOFF 4",
        "RTC_l1:", "PUSHOFF 4", "PUSHOFF 1", "LESS", "NOT", "JUMPC RTC_e1",
        "PUSHOFF 3", "PUSHOFF 4", "ADD", "PUSHOFF -3", "PUSHOFF 4", "ADD", "PUSHIND", "STOREIND",
        "PUSHOFF 4", "PUSHIMM 1", "ADD", "STOREOFF 4", "JUMP RTC_l1",
        "RTC_e1:", "PUSHIMM 0", "STOREOFF 4",
        "RTC_l2:", "PUSHOFF 4", "PUSHOFF 2", "PUSHIMM 1", "ADD", "LESS", "NOT", "JUMPC RTC_e2",
        "PUSHOFF 3", "PUSHOFF 1", "ADD", "PUSHOFF 4", "ADD", "PUSHOFF -2", "PUSHOFF 4", "ADD", "PUSHIND", "STOREIND",
        "PUSHOFF 4", "PUSHIMM 1", "ADD", "STOREOFF 4", "JUMP RTC_l2",
        "RTC_e2:", "PUSHOFF 3", "STOREOFF -3", "ADDSP -4", "UNLINK", "RST", "");

    // repeat(s,n): s at -3, n at -2.  locals: 1=len, 2=result, 3=i, 4=j, 5=p
    static final String REPEAT = String.join("\n",
        "RT_repeat:", "LINK", "ADDSP 5",
        "PUSHOFF -3", "JSR RT_strlen", "STOREOFF 1",
        "PUSHOFF -2", "ISNEG", "NOT", "JUMPC RTR_ok", "PUSHIMM 0", "STOREOFF -2",
        "RTR_ok:", "PUSHOFF 1", "PUSHOFF -2", "TIMES", "PUSHIMM 1", "ADD", "MALLOC", "STOREOFF 2",
        "PUSHIMM 0", "STOREOFF 3", "PUSHIMM 0", "STOREOFF 5",
        "RTR_o:", "PUSHOFF 3", "PUSHOFF -2", "LESS", "NOT", "JUMPC RTR_oe",
        "PUSHIMM 0", "STOREOFF 4",
        "RTR_i:", "PUSHOFF 4", "PUSHOFF 1", "LESS", "NOT", "JUMPC RTR_ie",
        "PUSHOFF 2", "PUSHOFF 5", "ADD", "PUSHOFF -3", "PUSHOFF 4", "ADD", "PUSHIND", "STOREIND",
        "PUSHOFF 5", "PUSHIMM 1", "ADD", "STOREOFF 5",
        "PUSHOFF 4", "PUSHIMM 1", "ADD", "STOREOFF 4", "JUMP RTR_i",
        "RTR_ie:", "PUSHOFF 3", "PUSHIMM 1", "ADD", "STOREOFF 3", "JUMP RTR_o",
        "RTR_oe:", "PUSHOFF 2", "PUSHOFF 5", "ADD", "PUSHIMM 0", "STOREIND",
        "PUSHOFF 2", "STOREOFF -3", "ADDSP -5", "UNLINK", "RST", "");

    // reverse(s): s at -2.  locals: 1=len, 2=result, 3=i
    static final String REVERSE = String.join("\n",
        "RT_reverse:", "LINK", "ADDSP 3",
        "PUSHOFF -2", "JSR RT_strlen", "STOREOFF 1",
        "PUSHOFF 1", "PUSHIMM 1", "ADD", "MALLOC", "STOREOFF 2",
        "PUSHIMM 0", "STOREOFF 3",
        "RTV_l:", "PUSHOFF 3", "PUSHOFF 1", "LESS", "NOT", "JUMPC RTV_e",
        "PUSHOFF 2", "PUSHOFF 3", "ADD",
        "PUSHOFF -2", "PUSHOFF 1", "PUSHOFF 3", "SUB", "PUSHIMM 1", "SUB", "ADD", "PUSHIND", "STOREIND",
        "PUSHOFF 3", "PUSHIMM 1", "ADD", "STOREOFF 3", "JUMP RTV_l",
        "RTV_e:", "PUSHOFF 2", "PUSHOFF 1", "ADD", "PUSHIMM 0", "STOREIND",
        "PUSHOFF 2", "STOREOFF -2", "ADDSP -3", "UNLINK", "RST", "");

    // cmp(a,b): a at -3, b at -2.  Returns -1/0/1.  locals: 1=i, 2=ca, 3=cb
    static final String CMP = String.join("\n",
        "RT_cmp:", "LINK", "ADDSP 3", "PUSHIMM 0", "STOREOFF 1",
        "RTS_l:", "PUSHOFF -3", "PUSHOFF 1", "ADD", "PUSHIND", "STOREOFF 2",
        "PUSHOFF -2", "PUSHOFF 1", "ADD", "PUSHIND", "STOREOFF 3",
        "PUSHOFF 2", "PUSHOFF 3", "LESS", "JUMPC RTS_lt",
        "PUSHOFF 2", "PUSHOFF 3", "GREATER", "JUMPC RTS_gt",
        "PUSHOFF 2", "ISNIL", "JUMPC RTS_eq",
        "PUSHOFF 1", "PUSHIMM 1", "ADD", "STOREOFF 1", "JUMP RTS_l",
        "RTS_lt:", "PUSHIMM -1", "STOREOFF -3", "JUMP RTS_end",
        "RTS_gt:", "PUSHIMM 1", "STOREOFF -3", "JUMP RTS_end",
        "RTS_eq:", "PUSHIMM 0", "STOREOFF -3",
        "RTS_end:", "ADDSP -3", "UNLINK", "RST", "");
}
