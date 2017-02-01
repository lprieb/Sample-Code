#!/usr/bin/env python2.7

import logging
import os
import socket
import sys
import argparse
import signal
import mimetypes

# Constants

ADDRESS  = ''
PORT     = 9235
BACKLOG  = 0
LOGLEVEL = logging.INFO
PROGRAM  = os.path.basename(sys.argv[0])


# BaseHandler Class

class BaseHandler(object):

    def __init__(self, fd, address, directory, port):
        ''' Construct handler from file descriptor and remote client address '''
        self.logger  = logging.getLogger()        # Grab logging instance
        self.socket  = fd                         # Store socket file descriptor
        self.address = '{}:{}'.format(*address)   # Store address
        self.stream  = self.socket.makefile('w+') # Open file object from file descriptor
        self.directory = directory
        self.port = port

        self.debug('Connect')

    def debug(self, message, *args):
        ''' Convenience debugging function '''
        message = message.format(*args)
        self.logger.debug('{} | {}'.format(self.address, message))

    def info(self, message, *args):
        ''' Convenience information function '''
        message = message.format(*args)
        self.logger.info('{} | {}'.format(self.address, message))

    def warn(self, message, *args):
        ''' Convenience warning function '''
        message = message.format(*args)
        self.logger.warn('{} | {}'.format(self.address, message))

    def error(self, message, *args):
        ''' Convenience error function '''
        message = message.format(*args)
        self.logger.error('{} | {}'.format(self.address, message))

    def exception(self, message, *args):
        ''' Convenience exception function '''
        message = message.format(*args)
        self.logger.error('{} | {}'.format(self.address, message))

    def handle(self):
        ''' Handle connection '''
        self.debug('Handle')
        raise NotImplementedError

    def finish(self):
        ''' Finish connection by flushing stream, shutting down socket, and
        then closing it '''
        self.debug('Finish')
        try:
            self.stream.flush()
            self.socket.shutdown(socket.SHUT_RDWR)
        except socket.error as e:
            pass    # Ignore socket errors
        finally:
            self.socket.close()

# HTTPHandler Class
class HTTPHandler(BaseHandler):
    def handle(self):
        data = line = self.stream.readline()
        while line != '\r\n':
            line = self.stream.readline()
            data += line

        # Parse HTTP request and headers
        self._parse_request(data)

        # Build uripath by normalizing REQUEST_URI
        self.fulldirectory = os.path.realpath(self.directory)
        self.fullpath = os.path.normpath(self.fulldirectory + os.environ['REQUEST_URI'])
        self.uripath = os.path.normpath(self.directory + os.environ['REQUEST_URI'])
        # Check path existence and types and then dispatch
        if not os.path.exists(self.fullpath) or  not self.fullpath.startswith(self.fulldirectory):
            self.debug("Handle Error 404")
            self._handle_error(404) # 404 error
        elif os.path.isfile(self.fullpath) and os.access(self.fullpath, os.X_OK):
            self.debug("Handle Executable File")
            self._handle_script()   # CGI script
        elif os.path.isfile(self.fullpath) and os.access(self.fullpath, os.R_OK):
            self.debug("Handle Static File")
            self._handle_file()     # Static file
        elif os.path.isdir(self.uripath) and os.access(self.uripath, os.R_OK):
            self.debug("Handle Directory")
            self._handle_directory()# Directory listing
        else:
            self.debug("Handle Error 403")
            self._handler_error(403)# 403 error


    def _parse_request(self, text):
        request_line = text.splitlines()[0]
        request_line = request_line.rstrip('\r\n')
        # Break down the request line into components
        self.request_method, self.uripath, self.request_version = request_line.split()
        self.debug("Parsing [{}, {}, {}]".format(self.request_method, self.uripath, self.request_version))

        os.environ["REQUEST_METHOD"] = self.request_method
        os.environ["REQUEST_URI"] = self.uripath.split("?",1)[0]
        os.environ["QUERY_STRING"] = self.uripath.split("?",1)[-1].rstrip("\r\n")

        for line in text.splitlines()[1:]:
            if line is not '':
                sbuffer = line.split(":",1)[0]
                sbuffer = sbuffer.replace("-","_")
                sbuffer = sbuffer.upper()
                sbuffer = "HTTTP_"+sbuffer
                os.environ[sbuffer] = line.split(":",1)[1]

        os.environ["REMOTE_ADDR"] = self.address
        os.environ["REMOTE_HOST"] = self.address
        os.environ["REMOTE_PORT"] = str(self.port)

    def _handle_file(self):
        mimetype, _ = mimetypes.guess_type(self.uripath)
        if mimetype is None:
            mimetype = 'application/octet-stream'
        self.stream.write('HTTP/1.0 200 OK\r\n')
        self.stream.write('Content-Type: {}\r\n\r\n'.format(mimetype))
        for line in open(self.uripath, "rb"):
            self.stream.write(line)

    def _handle_directory(self):
        # Send headers
        self.stream.write('HTTP/1.0 200 OK\r\n')
        self.stream.write('Content-Type: text/html\r\n\r\n')
        # Send Body
        # The following html code was adapted from original code from Professor Peter Bui at the University of Notre Dame
        self.stream.write('''<!DOCTYPE html>
<html lang="en">
<head>
<title>/</title>
<link href="https://www3.nd.edu/~pbui/static/css/blugold.css" rel="stylesheet">
<link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css" rel="stylesheet">
</head>
<body>
<div class="container">
<div class="page-header">
<h2>Directory Listing:'''+self.uripath+'''</h2>
</div>
<table class="table table-striped">
<thead>
<th>Type</th>
<th>Name</th>
<th>Size</th>
</thead>
<tbody>''')

        for tfile in sorted(os.listdir(self.uripath)):
            path = os.path.join(self.uripath, tfile)
            if os.path.isfile(path) and os.access(path, os.X_OK): #Executable
                self.stream.write('''<tr>
<td><i class="fa fa-file-code-o"></i></td>
<td><a href="'''+os.path.join(os.environ["REQUEST_URI"],tfile)+"/"+'">'+tfile+'''</a></td>
<td>'''+str(os.path.getsize(path))+'''</td>
</tr>''')
            elif os.path.isfile(path) and os.access(path, os.R_OK):# Text File
                self.stream.write('''<tr>
<td><i class="fa fa-file-text-o"></i></td>
<td><a href="'''+os.path.join(os.environ["REQUEST_URI"],tfile)+"/"+'">'+tfile+'''</a></td>
<td>'''+str(os.path.getsize(path))+'''</td>
</tr>''')
            elif os.path.isdir(path) and os.access(path, os.R_OK): # Directory
                self.stream.write('''<tr>
<td><i class="fa fa-folder-o"></i></td>
<td><a href="'''+os.path.join(os.environ["REQUEST_URI"],tfile)+"/"+'">'+tfile+'''</a></td>
<td>-</td>
</tr>''')

        self.stream.write('''</tbody>
</table>
</div>
</body>
</html>\n''')

    def _handle_script(self):
        signal.signal(signal.SIGCHLD, signal.SIG_DFL)
        for line in os.popen(self.uripath):
            self.stream.write(line)
        signal.signal(signal.SIGCHLD, signal.SIG_IGN)

    def _handle_error(self, error):
		# Send Headers
        self.stream.write('HTTP/1.0 200 OK\r\n')
        self.stream.write('Content-Type: text/html\r\n\r\n')
        # Send Body
        self.stream.write('''
<!DOCTYPE html>
<html lang="en">
<head>
<title>'''+str(error)+''' Error</title>
<link href="https://www3.nd.edu/~pbui/static/css/blugold.css" rel="stylesheet">
<link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css" rel="stylesheet">
</head>
<body>
<div class="container">
<div class="page-header">
<h2>404 Error</h2>
</div>
<div class="thumbnail">
    <img src="http://cdn.webfail.com/upl/img/ad09bbc7bd3/post2.jpg" class="img-responsive">
</div>
</div>
</body>
</html>''')


# TCPServer Class

class TCPServer(object):

    def __init__(self, directory, forking, port=PORT, address=ADDRESS, handler=HTTPHandler, ):
        ''' Construct TCPServer object with the specified address, port, and
        handler '''
        self.logger  = logging.getLogger()                              # Grab logging instance
        self.socket  = socket.socket(socket.AF_INET, socket.SOCK_STREAM)# Allocate TCP socket
        self.address = socket.gethostbyname(address)                    # Store address to listen on
        self.port    = port                                             # Store port to lisen on
        self.handler = handler                                          # Store handler for incoming connections
        self.directory = directory
        self.forking = forking

    def run(self):
        ''' Run TCP Server on specified address and port by calling the
        specified handler on each incoming connection '''
        try:
            # Bind socket to address and port and then listen
            self.socket.bind((self.address, self.port))
            self.socket.listen(BACKLOG)
            signal.signal(signal.SIGCHLD, signal.SIG_IGN)
        except socket.error as e:
            self.logger.error('Could not listen on {}:{}: {}'.format(self.address, self.port, e))
            sys.exit(1)

        self.logger.info('Listening on {}:{}...'.format(self.address, self.port))

        while True:
            # Accept incoming connection
            client, address = self.socket.accept()
            self.logger.debug('Accepted connection from {}:{}'.format(*address))

            # Instantiate handler, handle connection, finish connection

            if(not self.forking):
                try:
                    handler = self.handler(client, address, self.directory, self.port)
                    handler.handle()
                except Exception as e:
                    handler.exception('Exception: {}', e)
                finally:
                    handler.finish()
            else:
                pid = os.fork()
                if pid == 0:
                    try:
                        handler = self.handler(client, address, self.directory, self.port)
                        handler.handle()
                    except Exception as e:
                        handler.exception('Exception: {}', e)
                    finally:
                        handler.finish()
                        os._exit(0)
                else:
                    client.close()


# Main Execution

if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument("-d", metavar="DOCROOT", dest="directory", help="Set root directory (default is current directory)", default=".")
    parser.add_argument("-f", help="Enable forking mode", action="store_true", dest="forking")
    parser.add_argument("-v", dest="verbose", help="Set logging to DEBUG level", action="store_true")
    parser.add_argument("-p", metavar="PORT", dest="port", help="TCP Port to listen to (default is 9234)", default=PORT, type=int)

    args = parser.parse_args()

    if args.verbose:
        LOGLEVEL = logging.DEBUG

    # Set logging level
    logging.basicConfig(
        level   = LOGLEVEL,
        format  = '[%(asctime)s] %(message)s',
        datefmt = '%Y-%m-%d %H:%M:%S',
    )

    # Instantiate and run server
    server = TCPServer(args.directory, args.forking, args.port)

    try:
        server.run()
    except KeyboardInterrupt:
        sys.exit(0)
