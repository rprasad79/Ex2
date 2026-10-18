#!/bin/bash
# usage: tests/run_tests.sh  (from project root, after build.sh --stubs or a real build)
CP=${CP:-build}
pass=0; fail=0
cd "$(dirname "$0")"
while read name exp; do
  rm -f /tmp/out.sam
  java -cp ../$CP assignment2.LiveOak2Compiler $name.lo /tmp/out.sam 2>/tmp/err.txt
  if [ ! -f /tmp/out.sam ]; then echo "FAIL $name: compile error: $(cat /tmp/err.txt)"; fail=$((fail+1)); continue; fi
  got=$(python3 sam_emu.py /tmp/out.sam 2>&1 | sed 's/Exit Status: //')
  if [ "$got" == "$exp" ]; then pass=$((pass+1)); else echo "FAIL $name: expected $exp got $got"; fail=$((fail+1)); fi
done < expected.txt
echo "valid programs: $pass passed, $fail failed"
# error programs must be rejected (non-zero exit, no output file)
epass=0; efail=0
for f in err_*.lo; do
  rm -f /tmp/out.sam
  java -cp ../$CP assignment2.LiveOak2Compiler $f /tmp/out.sam 2>/tmp/err.txt; rc=$?
  if [ $rc -ne 0 ] && [ ! -f /tmp/out.sam ]; then epass=$((epass+1)); echo "  ok  $f -> $(cat /tmp/err.txt)"; else efail=$((efail+1)); echo "FAIL $f was accepted"; fi
done
echo "error programs: $epass rejected, $efail wrongly accepted"
