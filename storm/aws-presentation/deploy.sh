#!/bin/sh
./build.sh
s3cmd sync --acl-public -r ./target/ s3://izazi-aws-presentation
