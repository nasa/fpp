#!/bin/sh -e

export COMPILER_ROOT=`cd ../../../..; pwd`

export TOP_DIR=$PWD
export FPRIME_DIR=`cd ../fprime; pwd`
# F Prime dependencies with no commas, for easier maintenance
fprime_deps="
$FPRIME_DIR/Fw/Buffer/Buffer.fpp
$FPRIME_DIR/Fw/Cmd/Cmd.fpp
$FPRIME_DIR/Fw/Dp/Dp.fpp
$FPRIME_DIR/Fw/Log/Log.fpp
$FPRIME_DIR/Fw/Prm/Prm.fpp
$FPRIME_DIR/Fw/Time/Time.fpp
$FPRIME_DIR/Fw/Tlm/Tlm.fpp
$FPRIME_DIR/Fw/Types/Types.fpp
$FPRIME_DIR/Platform/PlatformTypes.fpp
$FPRIME_DIR/config/FpConfig.fpp
"
# Add commas
export FPRIME_DEPS=`echo $fprime_deps | sed 's/ /,/g'`
export FPRIME_GCC=$COMPILER_ROOT/scripts/fprime-gcc
export FPP_TO_CPP=$COMPILER_ROOT/bin/fpp-to-cpp
