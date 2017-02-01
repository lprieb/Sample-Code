#!/usr/bin/env python2.7

import os
import yaml
import argparse
import fnmatch
import re
import time
import sys
import shlex


# Process Command Line Arguments
parser = argparse.ArgumentParser()

parser.add_argument('-r', dest="RULES", default="rules.yaml")
parser.add_argument('-t', dest="SECONDS", type=int, default=2)
parser.add_argument('-v', dest="verbose", action='store_true')
parser.add_argument("DIRECTORIES", nargs='*', default='.')
args = parser.parse_args()
    
# Define Functions
def debug(message, *targs):
    if(args.verbose):
        print >> sys.stderr,  message.format(*targs)

def child(command):
    command_list = shlex.split(command)
    try:
        debug("Executing \"{}\"",command)
        os.execvp(command_list[0], command_list)
    except OSError as e:
        print >> sys.stderr, "Could not execute command: {}\nError: {}".format(command, e.strerror)
        sys.exit(1)

def parent(child_pid):
    debug("Waiting for child with pid: {}", child_pid)
    try:
        pid, status = os.wait();
        debug("Child Returned with exit status: {}", status)
    except:
        pid, status = os.wait();
        debug("Child processes returned with exit status: {}", status)

# Process Data
debug("Loading rules from file {}...".format(args.RULES))
rules_file = open(args.RULES)
rules = yaml.load(rules_file)
matched_files = dict()

# Get all filenames
def iterate_files():
    debug("Iterating through files in directory and assigning actions to each...")
    temp_matched_files = dict()
    for directory in args.DIRECTORIES:
        for root, dirs, files in os.walk(directory, followlinks=True):
            for tfile in files:
                path = os.path.join(root, tfile)
                for rule in rules:
                    try:
                        if fnmatch.fnmatch(tfile, rule["pattern"]) is True:
                            temp_matched_files[rule["action"].replace("{path}", path)] = path
                            # The above creates a dictionary with actions as keys. The actions will already be stored with
                            # the actual file name replacing {path}
                    except SyntaxError:
                        pass
                    try:
                        if re.search(rule["pattern"], tfile):
                            temp_matched_files[rule["action"].replace("{path}", path)] = path
                    except (SyntaxError, re.error):
                        pass
    return temp_matched_files

#Perform actions
try:
    while True:
        debug("Testing for changes in Files...")
        current_time = time.time()
        time.sleep(args.SECONDS)
        matched_files = iterate_files()
        for action, tfile in matched_files.iteritems():
            try:
                inode = os.stat(tfile)
            except OSError:
                print >>sys.stderr, "File {} could not be proccessed".format(tfile)
                continue
            if inode.st_mtime > current_time:
                debug("Changes in file {} detected", tfile)
                try:
                    debug("Forking...")
                    child_pid = os.fork()
                    if child_pid == 0:
                        child(action)
                    else:
                        parent(child_pid)
                except OSError as e:
                    print >> sys.stderr, "Could not fork system. Command not executed: {}\n Error:{}".format(action, e.strerror)
    
except KeyboardInterrupt:
    print ""
    sys.exit(0)
