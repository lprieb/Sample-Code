import pcapy
import argparse
import sys
import socket
import struct
import os
import shutil

ETH_LEN = 14
TCP_PROTOCOL = 6
IP_PROTOCOL = 8
 
class ip_header_wrap():
    def __init__(self):
        self.vhl = ''
        self.tos = '' # Type of service
        self.version = ''
        self.hl = 0 # Used to calculate size
        self.size = 0 # Size of IP header
        self.len= 0 # Total Length
        self.id = 0
        self.off = 0 #Offset field
        self.ttl = '' #Time to live
        self.prot = '' # Protocol
        self.checksum = 0
        source_addr = ""
        dest_addr = ""
        src_addr_str = ""
        dest_addr_str = ""
    def ip_to_str(self, ip):
        return "%i.%i.%i.%i" % (ord(ip[0]) , ord(ip[1]) , ord(ip[2]), ord(ip[3]))
    def print_relevant(self):
        print "IP Version: ", self.version
        print "IP Size", self.size
        print "IP Type of Service: ", self.tos
        print "IP Total Length ", self.len
        print "IP ID: ", self.id
        print "IP Time to Live: ", self.ttl
        print "IP Protocol: ", self.prot
        print "IP Source: ", self.src_addr_str
        print "IP Destiantion: ", self.dest_addr_str
        
class tcp_header_wrap():
    def __init__(self):
        self.src_port = 0
        self.dest_port = 0
        self.seq = 0L # Sequence Number
        self.ack = 0L # Acknowledgement Number
        self.off = '' # Offset
        self.flags = ''
        self.window = 0
        self.checksum = 0
        self.urg_ptr = 0 # Urgent Point
        self.len = 0
    def print_relevant(self):
        print "Source Port: ", self.src_port
        print "Destination Port: ", self.dest_port
        print "Sequence Number: ", self.seq
        print "Acknowledge Number: ", self.ack
        print "TCP flags: ", self.flags
        print "Window: ", self.window
        print "Urgent Pointer: ", self.urg_ptr

class eth_header_wrap():
    def __init__(self):
        self.dest_host_addr = ""
        self.src_host_addr = ""
        self.dest_host_addr_hex_str = ""
        self.src_host_addr_hex_str = ""
        self.eth_type = ""
    def eth_to_hex_str(self, eth_addr):
        # Takes an ethernet address in raw format and converts it to a colon separated hex string
        return "%.2x:%.2x:%.2x:%.2x:%.2x:%.2x" % (ord(eth_addr[0]) , ord(eth_addr[1]) , ord(eth_addr[2]), ord(eth_addr[3]), ord(eth_addr[4]) , ord(eth_addr[5]))
    def print_relevant(self):
        print "Host Destination Address: ", self.dest_host_addr_hex_str
        print "Host Source Address: ", self.src_host_addr_hex_str
        print "Ethernet Type: ", self.eth_type       

def list_devices():
    devs = pcapy.findalldevs()
    # print devices
    
    for dev in devs:
        print (dev)
        
def eth_to_hex_str(hex_num):
    return "%.2x:%.2x:%.2x:%.2x:%.2x:%.2x" % (ord(hex_num[0]) , ord(hex_num[1]) , ord(hex_num[2]), ord(hex_num[3]), ord(hex_num[4]) , ord(hex_num[5]))


def process_packet(packet, num, VERBOSE = False, POST = False):
# Returns number of successfully posted headers. 1 or 0

    eth = eth_header_wrap()
    ethernet_header = packet[:ETH_LEN]
    
    # Unpack commands and store values
    unpacked_eth = struct.unpack('!6s6sH' , ethernet_header)
    eth.dest_host_addr = unpacked_eth[0]
    eth.dest_host_addr_hex_str = eth.eth_to_hex_str(eth.dest_host_addr)
    eth.src_host_addr = unpacked_eth[1]
    eth.src_host_addr_hex_str = eth.eth_to_hex_str(eth.src_host_addr)
    eth.type = socket.ntohs(unpacked_eth[2])
    
    if eth.type == IP_PROTOCOL:
        ip = ip_header_wrap()
        ip_header = packet[ETH_LEN:ETH_LEN+20] #20 is minimum size for IP header
        unpacked_ip = struct.unpack('!BBHHHBBH4s4s' , ip_header)
        ip.vhl = unpacked_ip[0]
        ip.version = ip.vhl >> 4
        ip.hl = ip.vhl & 0xf
        ip.size = ip.hl * 4
        ip.tos = unpacked_ip[1] # Type of Service
        ip.length = unpacked_ip[2] # Total Length
        ip.iden = unpacked_ip[3]
        ip.off = unpacked_ip[4] # Offset field
        ip.ttl = unpacked_ip[5] # Time to live
        ip.prot = unpacked_ip[6] # protocol
        ip.sum = unpacked_ip[7] # Checksum
        ip.src_addr = unpacked_ip[8]
        ip.dest_addr = unpacked_ip[9]
        ip.src_addr_str = ip.ip_to_str(ip.src_addr)
        ip.dest_addr_str = ip.ip_to_str(ip.dest_addr)
        
        
        if ip.prot == TCP_PROTOCOL:
            t = ETH_LEN+ip.size
            tcp = tcp_header_wrap()
            tcp_header = packet[t:t+20]
            unpacked_tcp = struct.unpack('!HHLLBBHHH' , tcp_header)
            tcp.src_port = unpacked_tcp[0]
            tcp.dest_port = unpacked_tcp[1]
            tcp.seq = unpacked_tcp[2]
            tcp.ack = unpacked_tcp[3]
            tcp.off = unpacked_tcp[4]
            tcp.len = tcp.off >> 4 # Length of tcp header
            tcp.flags = unpacked_tcp[5]
            tcp.window = unpacked_tcp[6]
            tcp.checksum = unpacked_tcp[7]
            tcp.urg_pt = unpacked_tcp[8] # Urgent Pointer

            header_length = ETH_LEN + ip.size + tcp.len*4
            
            data = packet[header_length:]
            
            # Parse data
            lines = data.splitlines()
            if len(lines) > 0:
                f_line = lines[0].rstrip("\r\n")
                f_line_list = f_line.split()
                if len(f_line_list) > 2:  
                    if f_line_list[0].startswith("HTTP"):
                        if VERBOSE:
                            ip.print_relevant()
                            tcp.print_relevant()
                        # HTTP reply
                        print "{} {}:{} {}:{} HTTP Response\r\n".format(num,ip.src_addr_str,tcp.src_port,ip.dest_addr_str, tcp.dest_port),
                        offset = print_headers(data)
                        print "\r\n\r\n"
                        return 1
                    elif f_line_list[2].startswith("HTTP"):
                        # HTTP request
                        if VERBOSE:
                            ip.print_relevant()
                            tcp.print_relevant()
                        print "{} {}:{} {}:{} HTTP Request\r\n".format(num,ip.src_addr_str,tcp.src_port,ip.dest_addr_str, tcp.dest_port),
                        offset = print_headers(data)
                       
                        print "\r\n\r\n"
                        
                        if f_line_list[0] == "POST":
                            post_file(data, num, offset)
                            
                        return 1
                    else:
                        pass
    
    return 0 # No headers were posted

            
            
def print_headers(data):
# Function prints the HTTP data and returns the offset to print the body of the message
    offset = 0
    for line in data.splitlines():
        offset += len(line) + 1 # plus one to count for lost newline
        if line == "":
            break
        print line
        
    return offset

def post_file(data, num, offset = 0):
# Function creates files to write the body of POST requests
    if offset < 0:
        offset = 0
    
    filename = "post/"+str(num)+".txt"
    try:
        file_stream = os.open(filename, os.O_WRONLY|os.O_CREAT)
    except OSError as e:
        
        print "Could not create file {}. Error: {}".format(filename, e)
        return
    os.write(file_stream, data[offset:])
    os.close(file_stream)
    
        
    
            



if __name__ == "__main__":
    # Add command line arguments
    parser = argparse.ArgumentParser(description="Sniffer for network devices")

    parser.add_argument("--device","-d", default="eth0", help="set device to sniff (default=eth0", dest="device")
    parser.add_argument("--list","-l", action="store_true", help="list all available devices and quit")
    parser.add_argument("--verbose","-v",action="store_true", help="activate verbose mode")
    parser.add_argument("--interactive","-i",action="store_true", help="run program in interactive mode")
    parser.add_argument("--post","-p", action="store_true", help="Store the body of POST HTTP commands in a file names post/#.txt")
    
    
    args = parser.parse_args()

    # get all devices

    if args.list == True:
        if args.interactive == True:
            list_devices()
            sys.exit(0)
        
    if args.interactive == True:
        print "Here is the list of devices currently available in your machine:"
        list_devices()
        args.device = raw_input("Enter device you wish to sniff: ")
    # Start Session
    
    if args.post:
        if os.path.isdir("post"):
            shutil.rmtree("post")
        os.mkdir("post")
    
    
    
    ses = pcapy.open_live(args.device, 65536, 0, 1000)
    ses.setfilter("tcp port 80")
    
    num = 1 
    
    print"Sniffing is starting..."
    
    try:
     while(True):
        (header, packet) = ses.next()
        if len(packet) > 0:
            num += process_packet(packet, num, args.verbose, args.post)
    except KeyboardInterrupt:
        pass
            
# References
# http://www.binarytides.com/code-a-packet-sniffer-in-python-with-pcapy-extension/
# http://www.tcpdump.org/pcap.html

        

    
