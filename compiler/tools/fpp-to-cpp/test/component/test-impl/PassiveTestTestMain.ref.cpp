// ======================================================================
// \title  PassiveTestTestMain.cpp
// \author [user name]
// \brief  cpp file for PassiveTest component test main function
// ======================================================================

#include "PassiveTestTester.hpp"

TEST(Nominal, toDo) {
  PassiveTestTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
