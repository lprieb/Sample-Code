#!/usr/bin/env python

import os
import sys

# Define Functions
def usage():
    print('''Unknown argument: -h
Usage: dd.py options...

Options:

      if=FILE     Read from FILE instead of stdin
      of=FILE     Write to FILE instead of stdout

      count=N     Copy only N input blocks
      bs=BYTES    Read and write up to BYTES bytes at a time

      seek=N      Skip N obs-sized blocks at start of output
      skip=N      Skip N ibs-sized blocks at start of input''')

def open_fd(path, mode):
    try:
        return os.open(path, mode)
    except OSError as e:
        print >>sys.stderr, 'Could not open file {}: {}'.format(path, e)
        sys.exit(1)
def lseek_fd(fd, pos, how):
    try:
        return os.lseek(fd, pos, how)
    except OSError as e:
        print >>sys.stderr, "Could not skip source file {}: {}".format(path, e)
def lskip_fd(fd, pos, how):
    try:
        return os.lseek(fd, pos, how)
    except OSError as e:
        print >>sys.stderr, 'Could not skip destination file {}:{}'.format(path, e)

# Set Default Variable Values
IF_CALLED=0
OF_CALLED=0
BS=512
COUNT=sys.maxint
SEEK=0
SKIP=0

for i in range(len(sys.argv) - 1):
    temp = sys.argv[i+1].split('=')
    if len(temp) == 2:
        if temp[0] == "if":
            IF=temp[1]
            IF_CALLED=1
        elif temp[0] == "of":
            OF=temp[1]
            OF_CALLED=1
        elif temp[0] == "count":
            COUNT=int(temp[1])
        elif temp[0] == "bs":
            BS=int(temp[1])
        elif temp[0] == "seek":
            SEEK=int(temp[1])
        elif temp[0] == "skip":
            SKIP=int(temp[1])
    elif len(temp) == 1:
        if temp[0] == "-h":
            usage()
            sys.exit()

# Set File Descriptors
if IF_CALLED:
    IF_DES = open_fd(IF, os.O_RDONLY)
    TO_SKIP = 1
else:
    IF_DES = 0
    TO_SKIP = 0

if OF_CALLED:
    OF_DES = open_fd(OF, os.O_WRONLY|os.O_CREAT)
    TO_SEEK=1
else:
    OF_DES = 1
    TO_SEEK = 0

#Seeking and Skipping
if TO_SEEK:
    lseek_fd(OF_DES, BS*SEEK, os.SEEK_SET)
else:
    if SEEK != 0:
        print>>sys.stderr, "Can't seek stdout"
if TO_SKIP:
    lskip_fd(IF_DES, BS*SKIP, os.SEEK_SET)
else:
    if SKIP != 0:
        print>>sys.stderr, "Can't skip stdin"

#Copying Data
i=0
while i < COUNT:
    data = os.read(IF_DES, BS)
    if len(data) == 0:
       sys.exit()
    os.write(OF_DES, data)
    i = i + 1
    
