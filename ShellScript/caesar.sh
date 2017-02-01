#!/bin/sh

rotation=13

if [ $# -eq 1 ]; then
    rotation=$1
elif [ $# -gt 1 ]; then
    echo "Wrong number of arguments"
fi

rotation=$(expr $rotation % 26)

if [ $rotation -eq 0 ]; then
    cat
    exit
fi

zshift=$((rotation - 1))
lower_letters="bcdefghijklmnopqrstuvwxyza"

low_lowcase_bound=$(echo $lower_letters | cut -c$rotation)

if [ $rotation -eq 1 ]; then # special case which cause cut to fail because zshift is zero
	high_lowcase_bound="a"
else
	high_lowcase_bound=$(echo $lower_letters | cut -c$zshift)
fi



low_highcase_bound=$(echo $low_lowcase_bound | tr [a-z] [A-Z])
high_highcase_bound=$(echo $high_lowcase_bound | tr [a-z] [A-Z])

source="[a-zA-Z]"
target="[${low_lowcase_bound}-za-${high_lowcase_bound}${low_highcase_bound}-ZA-${high_highcase_bound}]"

tr $source $target
