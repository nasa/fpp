#!/bin/sh -e

export COMPILER_ROOT=`cd ../../../..; pwd`

export TOP_DIR=$PWD
export FPRIME_DIR=`cd ../fprime; pwd`
export FPRIME_DEPS="$FPRIME_DIR/config/FpConfig.fpp,$FPRIME_DIR/Platform/PlatformTypes.fpp,$FPRIME_DIR/Fw/Prm/Prm.fpp,$FPRIME_DIR/Fw/Cmd/Cmd.fpp,$FPRIME_DIR/Fw/Time/Time.fpp,$FPRIME_DIR/Fw/Tlm/Tlm.fpp"
export FPRIME_GCC=$COMPILER_ROOT/scripts/fprime-gcc
export FPP_TO_CPP=$COMPILER_ROOT/bin/fpp-to-cpp
