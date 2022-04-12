#!/bin/bash

if test "$#" -ne 3; then
    echo "Usage: ./compare.sh <stage> <correct_output_file> <output_from_your_program>"
    exit 1;
fi

stage_end=$(($1 + 1))
diff -B --strip-trailing-cr <(sed -n "/Stage $1/,/Stage $stage_end/p" $2 | sed \$d) $3
