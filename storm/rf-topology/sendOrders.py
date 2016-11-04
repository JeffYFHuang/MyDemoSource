import logging
import random
import sys
import uuid
import json

from kafka.client import KafkaClient, FetchRequest, ProduceRequest

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
            return map(lambda x: format % dist(mean, sigma), range(0, num_col)) + [order_id]
          
def send_order(kafka, content):
    message = kafka.create_message(json.dumps(content))
    request = ProduceRequest("orders", -1, [message])
    kafka.send_message_set(request)  
            
if __name__ == '__main__':
    num_row = int(sys.argv[1])
    num_col = int(sys.argv[2])
    kafka = KafkaClient("localhost", 9092)
	
    for i in range(0, num_row):
        send_order(kafka, gen_row(CUSTOMER_SEGMENTS, num_col))