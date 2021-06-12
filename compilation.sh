#!/bin/bash
cd
if [ -n "$1" ]; then
  cd $1
  g++ -Wall -o source source.cpp
else
  echo "no parameters"
fi
