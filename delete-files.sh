#!/bin/bash
cd
if [ -n "$1" ]
then
cd $1
rm -rf $1*;
else
echo "no parametors";
fi
