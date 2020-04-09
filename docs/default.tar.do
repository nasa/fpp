#!/bin/sh -e

redo-ifchange $2
tar -cf $3 $2
