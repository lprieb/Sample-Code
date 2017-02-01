#!/usr/bin/env python

import argparse
import sys
import os
import fnmatch
import re
import stat

# Parse Commandline Arguments
parser = argparse.ArgumentParser(add_help=False)
parser.add_argument('directory')
parser.add_argument('-type', choices=['f','d'], dest="type", default='b')
parser.add_argument('-executable', action='store_true', dest='executable')
parser.add_argument('-readable', action='store_true', dest='readable')
parser.add_argument('-writable', action='store_true', dest='writable')
parser.add_argument('-empty', action='store_true', dest='empty')
parser.add_argument('-name',dest='sh_name_pattern')
parser.add_argument('-path',dest='sh_path_pattern')
parser.add_argument('-regex',dest='regex_path_pattern')
parser.add_argument('-perm', dest='perm')
parser.add_argument('-newer', dest='newer_filename')
parser.add_argument('-uid', dest='uid')
parser.add_argument('-gid', dest='gid')
parser.add_argument('-h', dest='help',action='store_true')

args = parser.parse_args()

if args.help:
    print('''Usage: find.py directory [options]...

Options:

    -type [f|d]     File is of type f for regular file or d for directory

    -executable     File is executable and directories are searchable to user
    -readable       File readable to user
    -writable       File is writable to user

    -empty          File or directory is empty

    -name  pattern  Base of file name matches shell pattern
    -path  pattern  Path of file matches shell pattern
    -regex pattern  Path of file matches regular expression

    -perm  mode     File's permission bits are exactly mode (octal)
    -newer file     File was modified more recently than file

    -uid   n        File's numeric user ID is n
    -gid   n        File's numeric group ID is n''')
    sys.exit()


def getInode(path):
    try:
        inode = os.stat(path)
    except OSError:
        try:
            inode = os.lstat(path)
        except OSError:
            print>>sys.stderr, "Couldn't open {}".format(path)
    return inode

# Test if file should be printed
def include(root, basename):
    path = os.path.join(root, basename)
    inode = None
    if args.type == 'f':
        inode = getInode(path)
        if stat.S_ISREG(inode.st_mode) is False:
            return False
    if args.executable is True:
    	if os.access(path, os.X_OK) is False:
            return False
    if args.readable is True:
        if os.access(path, os.R_OK) is False:
            return False
    if args.writable is True:
        if os.access(path, os.W_OK) is False:
            return False
    if args.empty is True:
        if inode is None:
            inode = getInode(path) 
       
        if stat.S_ISDIR(inode.st_mode):
            try:
                filesInDir = os.listdir(path)
                if len(os.listdir(path)) != 0:
                    return False
            except OSError:
                print>>sys.stderr, "Couldn't access directory {}".format(path)
                return False
        if stat.S_ISREG(inode.st_mode):
            if inode.st_size != 0:
                return False
        if stat.S_ISLNK(inode.st_mode):
            return False
    if args.sh_name_pattern is not None:
        if fnmatch.fnmatch(basename, args.sh_name_pattern) is False:
            return False
    if args.sh_path_pattern is not None:
        if fnmatch.fnmatch(path, args.sh_path_pattern) is False:
            return False
    if args.regex_path_pattern is not None:
        if re.search(args.regex_path_pattern, path) is None:
            return False
    if args.perm is not None:
        if inode is None:
            inode = getInode(path)

        if int(args.perm,8) != stat.S_IMODE(inode.st_mode):
            return False
    if args.newer_filename is not None:
        if inode is None:
            inode = getInode(path)
        try:
            new_inode = os.stat(args.newer_filename)
            if inode.st_mtime <= new_inode.st_mtime:
                return False
        except OSError:
            print>>sys.stderr, "Bad argument to newer file"
    if args.uid is not None:
        if inode is None:
            inode = getInode(path)
        if inode.st_uid != int(args.uid):
            return False
    if args.gid is not None:
        if inode is None:
            inode = getInode(path)
        if inode.st_gid != int(args.gid):
            return False
    return True
		
# Print Files
# First Test given directory
if include(os.path.dirname(args.directory), os.path.basename(args.directory)):
    print args.directory
# Test all subfiles
for root, dirs, files in os.walk(args.directory, followlinks=True):
    if args.type == 'f' or args.type == 'b':
        for tfile in files:
	    if include(root, tfile):
		print os.path.join(root, tfile)
    if args.type == 'd' or args.type == 'b':
        for tdir in dirs:
            if include(root, tdir):
                print os.path.join(root, tdir)

                

