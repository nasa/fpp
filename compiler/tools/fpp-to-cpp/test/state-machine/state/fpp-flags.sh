test_dir=`(cd $PWD/../..; pwd)`
fprime_dir=$test_dir/fprime
config_dir=$fprime_dir/config
platform_dir=$fprime_dir/Platform
harness_dir=$test_dir/state-machine/harness
fpp_flags="
-p $fprime_dir,$test_dir
-i $harness_dir/harness.fpp,$config_dir/FpConfig.fpp,$config_dir/FpConstants.fpp,$config_dir/ComCfg.fpp,$platform_dir/PlatformTypes.fpp
"
