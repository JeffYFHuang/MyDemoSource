#!/usr/bin/env python

import sys
import os

filepath = os.environ["map_input_file"]
filename = os.path.split(filepath)[-1]
# Read each line from stdin
for line in sys.stdin:

  # Get the words in each line
  words = line.split()

  # Generate the count for each word
  for word in words:

     # Write the key-value pair to stdout to be processed by 
     # the reducer.
     # The key is anything before the first tab character and the 
     #value is anything after the first tab character.
     print '{0}\t{1}'.format(filename, word)
