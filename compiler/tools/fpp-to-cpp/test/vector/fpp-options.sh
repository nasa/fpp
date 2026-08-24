test_dir=`(cd $PWD/..; pwd)`
fprime_dir=$test_dir/fprime
config_dir=$fprime_dir/config
platform_dir=$fprime_dir/Platform
fpp_options="
-p $PWD,$fprime_dir
-i $config_dir/FpConfig.fpp,$config_dir/FpConstants.fpp,$config_dir/ComCfg.fpp,$platform_dir/PlatformTypes.fpp
"
