#!/bin/sh
cd `dirname $0`

exec 1>&2
redo-always
redo-ifchange T.hpp
../../../../compiler/scripts/fprime-gcc T.hpp
