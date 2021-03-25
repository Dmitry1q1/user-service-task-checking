#!/bin/bash
cd
if [ -n "$1" ]
then
cd $1
./source
else
echo "no parameters error.";
fi
