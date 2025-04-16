// ======================================================================
// \title  ActiveExternalParamsTestMain.cpp
// \author [user name]
// \brief  cpp file for ActiveExternalParams component test main function
// ======================================================================

#include "ActiveExternalParamsTester.hpp"

TEST(Nominal, toDo) {
  ActiveExternalParamsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
