// ======================================================================
// \title  ActiveAsyncProductPortsOnlyTestMain.cpp
// \author [user name]
// \brief  cpp file for ActiveAsyncProductPortsOnly component test main function
// ======================================================================

#include "ActiveAsyncProductPortsOnlyTester.hpp"

TEST(Nominal, toDo) {
  ActiveAsyncProductPortsOnlyTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
