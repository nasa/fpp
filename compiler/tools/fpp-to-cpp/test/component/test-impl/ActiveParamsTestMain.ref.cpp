// ======================================================================
// \title  ActiveParamsTestMain.cpp
// \author [user name]
// \brief  cpp file for ActiveParams component test main function
// ======================================================================

#include "ActiveParamsTester.hpp"

TEST(Nominal, toDo) {
  ActiveParamsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
