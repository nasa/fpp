// ======================================================================
// \title  PassiveParamsTestMain.cpp
// \author [user name]
// \brief  cpp file for PassiveParams component test main function
// ======================================================================

#include "PassiveParamsTester.hpp"

TEST(Nominal, toDo) {
  PassiveParamsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
