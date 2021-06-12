#!/bin/bash
cd
if [ -n "$1" ]; then
  cd $1
  java $2
else
  echo "no parameters error."
fi
