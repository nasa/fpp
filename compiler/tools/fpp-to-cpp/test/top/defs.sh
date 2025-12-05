#!/bin/sh -e

export COMPILER_ROOT=`cd ../../../..; pwd`

export FPRIME_DIR=`cd ../fprime; pwd`
export FPRIME_DEPS="$FPRIME_DIR/config/FpConfig.fpp,$FPRIME_DIR/Platform/PlatformTypes.fpp,$FPRIME_DIR/Fw/Prm/Prm.fpp,$FPRIME_DIR/Fw/Cmd/Cmd.fpp"
