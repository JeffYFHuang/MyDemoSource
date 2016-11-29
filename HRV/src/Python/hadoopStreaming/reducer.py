#!/usr/bin/env python

import sys

curr_word = None
curr_counts = []

# Process each key-value pair from the mapper
for line in sys.stdin:

  # Get the key and value from the current line
  word, count = line.split('\t')

  # Convert the count to an int
  try:
     count = float(count)
  except:
     continue

  # If the current word is the same as the previous word, 
  # increment its count, otherwise print the words count 
  # to stdout
  if word == curr_word:
     curr_counts.append(count)
  else:

     # Write word and its number of occurrences as a key-value 
     # pair to stdout
     if curr_word:
        curr_counts.sort()
        curr_counts = ",".join("{0}".format(x) for x in curr_counts)
        print '{0}\t{1}'.format(curr_word, curr_counts)

     curr_word = word
     curr_counts = []
     curr_counts.append(count)

# Output the count for the last word
if curr_word == word:
  curr_counts.sort()
  curr_counts = ",".join("{0}".format(x) for x in curr_counts)
  print '{0}\t{1}'.format(curr_word, curr_counts)
