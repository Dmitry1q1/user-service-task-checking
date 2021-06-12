#!/bin/bash
cd
if [ -n "$1" ]; then
  echo $1
  echo $2
  javac $2
else
  echo "no parameters"
fi
