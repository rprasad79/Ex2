#!/usr/bin/env python3
"""Minimal SaM emulator (subset used by the compiler) for local testing only.
Prints the top of stack at STOP, like SamText's 'Exit Status'."""
import sys, re

def unescape(s):
    out = []; i = 0
    while i < len(s):
        c = s[i]
        if c == '\\':
            i += 1; e = s[i]
            if e == 'n': out.append('\n')
            elif e == 't': out.append('\t')
            elif e == 'r': out.append('\r')
            elif e.isdigit():
                out.append(chr(int(s[i:i+3], 8))); i += 2
            else: out.append(e)
        else: out.append(c)
        i += 1
    return ''.join(out)

def run(path, limit=5_000_000):
    prog, labels = [], {}
    for raw in open(path):
        line = raw.strip()
        if not line: continue
        if line.endswith(':') and ' ' not in line:
            labels[line[:-1]] = len(prog); continue
        m = re.match(r'(\w+)\s*(.*)', line)
        op, arg = m.group(1), m.group(2)
        prog.append((op, arg))
    mem = {}; sp = 0; fbr = 0; pc = 0; heap = 100000
    def push(v):
        nonlocal sp; mem[sp] = v; sp += 1
    def pop():
        nonlocal sp
        if sp <= 0: raise RuntimeError("stack underflow")
        sp -= 1; return mem.get(sp, 0)
    def target(a): return labels[a] if a in labels else int(a)
    steps = 0
    while True:
        steps += 1
        if steps > limit: raise RuntimeError("instruction limit")
        op, arg = prog[pc]; pc += 1
        if op == 'PUSHIMM': push(int(arg))
        elif op == 'PUSHIMMSTR':
            s = unescape(arg[1:-1]); a = heap
            for ch in s: mem[heap] = ord(ch); heap += 1
            mem[heap] = 0; heap += 1; push(a)
        elif op == 'PUSHOFF': push(mem.get(fbr + int(arg), 0))
        elif op == 'STOREOFF': mem[fbr + int(arg)] = pop()
        elif op == 'ADDSP': sp += int(arg)
        elif op == 'LINK': push(fbr); fbr = sp - 1
        elif op == 'UNLINK': fbr = pop()
        elif op == 'JSR': push(pc); pc = target(arg)
        elif op in ('RST', 'JUMPIND'): pc = pop()
        elif op == 'JUMP': pc = target(arg)
        elif op == 'JUMPC':
            if pop() != 0: pc = target(arg)
        elif op == 'MALLOC':
            n = pop(); a = heap; heap += n; push(a)
        elif op == 'PUSHIND': push(mem.get(pop(), 0))
        elif op == 'STOREIND':
            v = pop(); a = pop(); mem[a] = v
        elif op == 'NOT': push(1 if pop() == 0 else 0)
        elif op == 'ISNIL': push(1 if pop() == 0 else 0)
        elif op == 'ISNEG': push(1 if pop() < 0 else 0)
        elif op in ('ADD','SUB','TIMES','DIV','MOD','LESS','GREATER','EQUAL','AND','OR'):
            b = pop(); a = pop()
            if op == 'ADD': push(a + b)
            elif op == 'SUB': push(a - b)
            elif op == 'TIMES': push(a * b)
            elif op == 'DIV':
                if b == 0: raise RuntimeError("div by zero")
                q = abs(a) // abs(b); push(q if (a < 0) == (b < 0) else -q)
            elif op == 'MOD':
                if b == 0: raise RuntimeError("mod by zero")
                r = abs(a) % abs(b); push(r if a >= 0 else -r)
            elif op == 'LESS': push(1 if a < b else 0)
            elif op == 'GREATER': push(1 if a > b else 0)
            elif op == 'EQUAL': push(1 if a == b else 0)
            elif op == 'AND': push(1 if a and b else 0)
            elif op == 'OR': push(1 if a or b else 0)
        elif op == 'STOP': return mem.get(sp - 1, 0), sp
        else: raise RuntimeError("unsupported instruction " + op)

if __name__ == '__main__':
    v, sp = run(sys.argv[1]); print("Exit Status:", v)
    sys.exit(0)
