#!/bin/sh

# manage flags
MAX=10
DUFLAGS=-h

while [ $# -ne 0 ]; do
    case $1 in
	-a) shift
	    DUFLAGS="$DUFLAGS -a" ;;
	-n) shift 
	    MAX=$1
	    shift  ;;
	*) if [ -d $! ]; then
		DIRECTORY="$DIRECTORY $1"
		shift
	   else
		echo "Could not find directory $!"
		exit 1
	   fi ;;
    esac
done

if [ "$DIRECTORY" = "" ]; then
    echo "No Directory was provided"
    exit 2
fi


for i in $DIRECTORY; do
    echo "$(du $DUFLAGS $i 2>/dev/null | sort -rh | head -$MAX)"
done


