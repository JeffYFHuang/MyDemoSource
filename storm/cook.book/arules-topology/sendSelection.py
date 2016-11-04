import logging
import random
import sys
import uuid
import json

from kafka.client import KafkaClient, FetchRequest, ProduceRequest

products = ["citrus fruit","semi-finished bread","margarine","ready soups","tropical fruit","yogurt","coffee","whole milk","pip fruit","cream cheese ","meat spreads","other vegetables","condensed milk","long life bakery product","butter","rice","abrasive cleaner","rolls/buns","UHT-milk","bottled beer","liquor (appetizer)","pot plants","cereals","white bread","bottled water","chocolate","curd","flour","dishes","beef","frankfurter","soda","chicken","sugar","fruit/vegetable juice","newspapers","packaged fruit/vegetables","specialty bar","butter milk","pastry","processed cheese","detergent","root vegetables","frozen dessert","sweet spreads","salty snack","waffles","candy","bathroom cleaner","canned beer","sausage","brown bread","shopping bags","beverages","hamburger meat","spices","hygiene articles","napkins","pork","berries","whipped/sour cream","artif. sweetener","grapes","dessert","zwieback","domestic eggs","spread cheese","misc. beverages","hard cheese","cat food","ham","turkey","baking powder","pickled vegetables","oil","chewing gum","chocolate marshmallow","ice cream","frozen vegetables","canned fish","seasonal products","curd cheese","red/blush wine","frozen potato products","specialty fat","specialty chocolate","candles","flower (seeds)","sparkling wine","salt","frozen meals","canned vegetables","onions","herbs","white wine","brandy","photo/film","sliced cheese","pasta","softener","cling film/bags","fish","male cosmetics","canned fruit","Instant food products","soft cheese","honey","dental care","popcorn","cake bar","snack products","flower soil/fertilizer","specialty cheese","finished products","cocoa drinks","dog food","prosecco","frozen fish","make up remover","cleaner","female sanitary products","dish cleaner","cookware","meat","tea","mustard","house keeping products","skin care","potato products","liquor","pet care","soups","rum","salad dressing","sauces","vinegar","soap","hair spray","instant coffee","roll products ","mayonnaise","rubbing alcohol","syrup","liver loaf","baby cosmetics","organic products","nut snack","kitchen towels","frozen chicken","light bulbs","ketchup","jam","decalcifier","nuts/prunes","liqueur","organic sausage","cream","toilet cleaner","specialty vegetables","baby food","pudding powder","tidbits","whisky","frozen fruits","bags","cooking chocolate","sound storage medium","kitchen utensil","preservation products"]

def get_values():
	vals = []
	for i in range(0,random.randint(2,5)):
		rn = random.randint(0,len(products)-1)
		candidate = products[rn]
		while vals.count(candidate) == 1:
			rn = random.randint(0,len(products)-1)
			candidate = products[rn]
		vals.append(candidate)
	return vals

def gen_row():
	row = get_values()
	row.insert(0,str(uuid.uuid1()).split("-")[0])
	return row
          
def send_order(kafka, content):
    message = kafka.create_message(json.dumps(content))
    request = ProduceRequest("transactions", -1, [message])
    kafka.send_message_set(request)  
            
if __name__ == '__main__':
    num_row = int(sys.argv[1])
    kafka = KafkaClient("localhost", 9092)
	
    for i in range(0, num_row):
        send_order(kafka, gen_row())