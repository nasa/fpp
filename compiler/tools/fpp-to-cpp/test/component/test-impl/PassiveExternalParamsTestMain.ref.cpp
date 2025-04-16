// ======================================================================
// \title  PassiveExternalParamsTestMain.cpp
// \author [user name]
// \brief  cpp file for PassiveExternalParams component test main function
// ======================================================================

#include "PassiveExternalParamsTester.hpp"

TEST(Nominal, toDo) {
  PassiveExternalParamsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
