// ======================================================================
// \title  QueuedTestTestMain.cpp
// \author [user name]
// \brief  cpp file for QueuedTest component test main function
// ======================================================================

#include "QueuedTestTester.hpp"

TEST(Nominal, toDo) {
  QueuedTestTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
