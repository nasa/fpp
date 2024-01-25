// ======================================================================
// \title  ActiveTestTestMain.cpp
// \author [user name]
// \brief  cpp file for ActiveTest component test main function
// ======================================================================

#include "ActiveTestTester.hpp"

TEST(Nominal, toDo) {
  M::ActiveTestTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
