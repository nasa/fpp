#!/bin/sh

redo-ifchange `find . -name '*.hpp'` *.cpp
g++ -I . *.cpp -o $3
