#!/usr/bin/env python

import sys
import numpy as np
from DataModel import DM

curr_word = None
curr_counts = []

dm=DM(False)

def getDatabyInterval(data, start, end):
    if end < data[-1]:
          datasec=[data[i] for i in range(len(data)) if data[i]>=start and data[i]<end]

    return np.array(datasec)

def getHRVInfo(datasec):
    dm.ClearAll()
    settings = getSettings()
    dm.LoadBeatSec(datasec, settings)
    dm.FilterNIHR()
    dm.InterpolateNIHR()
    dm.CalculateFrameBasedParams(showProgress=False)
    result = {}
    info = dm.GetInfoTime()
    result["TimeDomain"] = info
    info = dm.GetInfoNonLinear()
    result["NonLinear"] = info
    info = dm.GetInfoFB()
    result["FrequncyDomain"] = info
 
    return result

def getSettings():
    factorySettings = {}
    factorySettings['interpfreq'] = 4.0
    factorySettings['windowsize'] = 120.0
    factorySettings['windowshift'] = 60.0
    factorySettings['ulfmin'] = 0.0
    factorySettings['ulfmax'] = 0.03
    factorySettings['vlfmin'] = 0.03
    factorySettings['vlfmax'] = 0.05
    factorySettings['lfmin'] = 0.05
    factorySettings['lfmax'] = 0.15
    factorySettings['hfmin'] = 0.15
    factorySettings['hfmax'] = 0.4

    return factorySettings

def calculateHRV(data, winsize=300, shift=300):
    winmin=data[0]
    winmax=winsize
    results = []
    while winmax < data[-1]:
         datasec=getDatabyInterval(data, winmin, winmax)
         result = {}
         result["startTime"] = winmin
         result["endTime"] = winmax
         result["HRV"] = getHRVInfo(np.array(datasec))
         results.append(result)
         winmin=winmin+shift
         winmax=winmin+winsize

    return results

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
        data = calculateHRV(curr_counts)
#        curr_counts = ",".join("{0}".format(x) for x in curr_counts)
        print '{0}\t{1}'.format(curr_word, data)

     curr_word = word
     curr_counts = []
     curr_counts.append(count)

# Output the count for the last word
if curr_word == word:
  curr_counts.sort()
  data = calculateHRV(curr_counts)
#  curr_counts = ",".join("{0}".format(x) for x in curr_counts)
  print '{0}\t{1}'.format(curr_word, data)
