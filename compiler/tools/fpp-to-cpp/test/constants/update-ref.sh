constants()
{
  update "-n constants.names.txt -p $PWD" constants
  mv constants.names.txt constants.names.ref.txt
  remove_year < FppConstantsAc.hpp > FppConstantsAc_constants.ref.hpp
  remove_year < FppConstantsAc.cpp > FppConstantsAc_constants.ref.cpp
}

constants_guard()
{
  update "-g GUARD_PREFIX -p $PWD" constants
  remove_year < FppConstantsAc.hpp > FppConstantsAc_constants_guard.ref.hpp
  remove_year < FppConstantsAc.cpp > FppConstantsAc_constants_guard.ref.cpp
}

constants_output_dir()
{
  update "-d output_dir -p $PWD" constants output_dir/constants
  remove_year < output_dir/FppConstantsAc.hpp > output_dir/FppConstantsAc.ref.hpp
  remove_year < output_dir/FppConstantsAc.cpp > output_dir/FppConstantsAc.ref.cpp
}

constants_string()
{
  update "-p $PWD" constants_string
  remove_year < FppConstantsAc.hpp > FppConstantsAc_constants_string.ref.hpp
  remove_year < FppConstantsAc.cpp > FppConstantsAc_constants_string.ref.cpp
}
