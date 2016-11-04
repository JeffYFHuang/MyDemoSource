import logging
import random
import sys
import uuid
import json

from sys import stdout

CUSTOMER_SEGMENTS = (
    [0.2, ["0", random.gauss, 0.25, 0.75, "%0.2f"]],
    [0.8, ["0", random.gauss, 1.5, 0.25, "%0.2f"]],
    [0.9, ["1", random.gauss, 0.6, 0.2, "%0.2f"]],
    [1.0, ["1", random.gauss, 0.75, 0.2, "%0.2f"]]
)

def gen_row (segments, num_col):
    coin_flip = random.random()

    for prob, rand_var in segments:
        if coin_flip <= prob:
            (label, dist, mean, sigma, format) = rand_var
            order_id = str(uuid.uuid1()).split("-")[0]
            return map(lambda x: format % dist(mean, sigma), range(0, num_col)) + [label]

def print_row (segments, num_col):
    stdout.write("testData.put(\"")	
    order_id = str(uuid.uuid1()).split("-")[0]
    stdout.write( order_id )
    stdout.write( "\", new Object[]{" )
    values =  gen_row(segments, num_col)
    if values[len(values)-1] == "0":
    	stdout.write( "\"Hub1\"" )
    else:
    	stdout.write( "\"Hub2\"" )
    stdout.write( ", new double[] { " )
    for x in range(0, len(values)-1):
        stdout.write( str(values[x]) )
        if x < (len(values)-2):
            stdout.write( "," )
    stdout.write( "}});\n" )

if __name__ == '__main__':
    for i in range(0, 100):
        print_row(CUSTOMER_SEGMENTS, 10)