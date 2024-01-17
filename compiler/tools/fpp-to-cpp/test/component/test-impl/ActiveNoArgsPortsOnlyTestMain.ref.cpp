// ======================================================================
// \title  ActiveNoArgsPortsOnlyTestMain.cpp
// \author [user name]
// \brief  cpp file for ActiveNoArgsPortsOnly component test main function
// ======================================================================

#include "ActiveNoArgsPortsOnlyTester.hpp"

TEST(Nominal, toDo) {
  ActiveNoArgsPortsOnlyTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
