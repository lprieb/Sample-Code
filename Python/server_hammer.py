#!/usr/bin/env python2.7

import argparse
import logging
import os
import socket
import sys
import signal
import time

# Constants

PROGRAM  = os.path.basename(sys.argv[0])
LOGLEVEL = logging.INFO
ADDRESS = "127.0.0.1"
PORT = 80

# TCPClient Class

class TCPClient(object):

    def __init__(self, address=ADDRESS, port=PORT):
        ''' Construct TCPClient object with the specified address and port '''
        self.logger  = logging.getLogger()                              # Grab logging instance
        self.socket  = socket.socket(socket.AF_INET, socket.SOCK_STREAM)# Allocate TCP socket
        self.address = address                                          # Store address to listen on
        self.port    = port                                             # Store port to lisen on

    def handle(self):
        ''' Handle connection '''
        self.logger.debug('Handle')
        raise NotImplementedError

    def run(self):
        ''' Run client by connecting to specified address and port and then
        executing the handle method '''
        try:
            # Connect to server with specified address and port, create file object
            self.logger.debug("Connecting to server at {}:{}".format(self.address, self.port))
            self.socket.connect((self.address, self.port))
            self.stream = self.socket.makefile('w+')
        except socket.error as e:
            self.logger.error('Could not connect to {}:{}: {}'.format(self.address, self.port, e))
            sys.exit(1)

        self.logger.debug('Connected to {}:{}...'.format(self.address, self.port))

        # Run handle method and then the finish method
        try:
            self.handle()
        except Exception as e:
            self.logger.exception('Exception: {}'.format(e))
        finally:
            self.finish()

    def finish(self):
        ''' Finish connection '''
        self.logger.debug('Finish')
        try:
            self.socket.shutdown(socket.SHUT_RDWR)
        except socket.error:
            pass    # Ignore socket errors
        finally:
            self.socket.close()



# HTTP Client Class
class HTTPClient(TCPClient):
    def __init__ (self, url):
        self.logger = logging.getLogger()
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.url = url
        self.url = url.split('://')[-1]
        
        self.port = PORT
                
        if '/' not in self.url:
			self.path = '/'
			if ':' in self.url:
			    self.host = self.url.split(":")[0]
			    self.port = int(self.url.split(":")[1])
			else:
			    self.host = self.url
        else:
			self.path = '/' + self.url.split('/', 1)[-1]
			self.host = self.url.split('/',1)[0]
			if ':' in self.host:
			    self.port = int(self.host.split(':',1)[1])
			    self.host = self.host.split(':',1)[0]
		
        
       
        try:
            self.address = socket.gethostbyname(self.host)
        except socket.gaierror as e:
            logging.error('Unable to lookup {}: {}'.format(self.address, e))
            sys.exit(1)

 
        self.logger.debug("URL: {}".format(self.url))
        self.logger.debug("HOST: {}".format(self.host))
        self.logger.debug("PORT: {}".format(self.port))
        self.logger.debug("PATH: {}".format(self.path))
    
    def handle(self):
        self.logger.debug('Handle') 
        # Send Request Following HTML Protocol
        try:
            self.logger.debug("Seding request...")
            request = ["GET ", self.path, " HTTP/1.0", "\r\n","HOST:{url}".format(url=self.host), "\r\n\r\n"]
            self.stream.write(''.join(request))
            self.stream.flush()
            # Read data sent from server
            self.logger.debug("Receiving response...")
            data = self.stream.readline()
        
            while data != "":
                sys.stdout.write(data)        
                data = self.stream.readline()
            return 
        except socket.error:
            pass

# Main Execution



if __name__ == '__main__':
    
    parser = argparse.ArgumentParser()

    parser.add_argument("-v", dest="verbose", action='store_true', help="Turn on verbose mode")
    parser.add_argument("-r", dest="REQUESTS", help="Number of requests made per process (default is 1)", default=1, type=int)
    parser.add_argument("-p", dest="PROCESSES", help="Number of processes (default is 1)", default=1, type=int)
    parser.add_argument("URL", help="URL of host to hammer")
    arguments = parser.parse_args()
    if(arguments.verbose):
        LOGLEVEL = logging.DEBUG

    # Set logging level
    logging.basicConfig(
        level   = LOGLEVEL,
        format  = '[%(asctime)s] %(message)s',
        datefmt = '%Y-%m-%d %H:%M:%S',
    )


    # Instantiate and run client
    #signal.signal(signal.SIGCHLD, p_waiter)
    forking_count = 0 #Counts number of Succesful forks
    try:
        for process in range(arguments.PROCESSES):
            try:
                pid = os.fork()
                if pid == 0:
                    pid = os.getpid()
                    total_time = 0
                    for request in range(arguments.REQUESTS):
                        client = HTTPClient(arguments.URL)
                        start = time.time()
                        client.run()
                        end = time.time()
                        elapsed_time = end - start
                        logging.debug("{} | Elapsed time: {} second".format(pid, elapsed_time))
                        total_time = total_time + elapsed_time
                    logging.debug("{} | Average elapsed time: {} seconds".format(pid, total_time/arguments.REQUESTS))
                    sys.exit(0)
    
                forking_count = forking_count + 1
                    # Parent waits for all children
            except OSError as e:
                logging.error("Could not fork system. Error: {}".format(e))
        for process in range(forking_count):
            try:
                os.wait()
            except OSError:
                os.wait() #Wait again if it gets interrupted 
    except KeyboardInterrupt:
        sys.exit(0)
