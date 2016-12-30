#!/bin/bash

#
# Copyright 2013-2014 by Think Big Analytics
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# bootstrap-r-rmr2.sh is a bootstrap script for Amazon's Elastic MapReduce service.
# By Jeffrey Breen <jeffrey.breen@thinkbiganalytics.com>, and based on work
# in JD Long's segue package, and Antonio Piccobolo's whirr script in RHadoop.


# turn on logging and exit on error
set -e -x

sudo tee /etc/apt/sources.list.d/R.list <<EOF
# debian R upgrade
deb http://cran.rstudio.com/bin/linux/ubuntu trusty/
EOF

# add key to keyring
sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys E084DAB9

sudo apt-get update

sudo apt-get install -y libssl-dev
sudo add-apt-repository -y ppa:openjdk-r/ppa  
sudo apt-get update   
sudo apt-get install -y openjdk-7-jdk

sudo rm /usr/lib/jvm/default-java
sudo ln /usr/lib/jvm/java-7-openjdk-amd64/ -s /usr/lib/jvm/default-java
# install R using the FRONTEND call to eliminate
# user interactive requests
sudo apt-get install -y r-base
sudo apt-get install -y r-base-dev r-cran-hmisc

# RCurl needs curl-config:
sudo apt-get install -y libcurl4-openssl-dev
sudo apt-get install -y mesa-common-dev libx11-dev mesa-common-dev libglu1-mesa-dev tk-dev
dpkg -S /usr/include/GL/gl.h
# some packages have trouble installing without this link
sudo apt-get install libgfortran3

file="/usr/lib/libgfortran.so"
if [ -f "$file" ]
then
    sudo rm $file
    sudo ln -s /usr/lib/x86_64-linux-gnu/libgfortran.so.3 $file
fi

file="/usr/lib/libquadmath.so"
if [ -f "$file0" ]
then
   sudo rm $file
   sudo ln -s /usr/lib/x86_64-linux-gnu/libquadmath.so.0 $file
fi

# for the package update script to run, the hadoop user needs to own the R library
sudo chown -R hduser /usr/local/lib/R

# Install rmr2's prerequisite packages from CRAN, plus plyr and some other favorites:
sudo R --no-save << EOF
install.packages(c("rJava", "Rcpp", "RJSONIO", "bitops", "digest",
                   "functional", "stringr", "plyr", "reshape2", "dplyr",
                   "R.methodsS3", "caTools", "Hmisc", "data.table", "rjson", "memoise", "RHRV", "rjson", "randomForest"),
    repos="http://cran.revolutionanalytics.com", INSTALL_opts=c('--byte-compile') )
EOF


# download and install rmr2 2.3.0 from github:

rm -rf RHadoop
mkdir RHadoop
cd RHadoop
curl --insecure -L https://github.com/RevolutionAnalytics/rmr2/releases/download/3.3.1/rmr2_3.3.1.tar.gz | tar zx
sudo R CMD INSTALL --byte-compile rmr2

curl --insecure -L https://github.com/RevolutionAnalytics/rhdfs/blob/master/build/rhdfs_1.0.8.tar.gz?raw=true | tar zx
sudo -E R CMD INSTALL --byte-compile rhdfs

#sudo su << EOF1 
#echo ' 
#export HADOOP_HOME=/usr/lib/hadoop
#' >> /etc/profile 
 
#EOF1
