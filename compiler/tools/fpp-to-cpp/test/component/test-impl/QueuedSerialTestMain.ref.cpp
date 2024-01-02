// ======================================================================
// \title  QueuedSerialTestMain.cpp
// \author [user name]
// \brief  cpp file for QueuedSerial component test main function
// ======================================================================

#include "QueuedSerialTester.hpp"

TEST(Nominal, toDo) {
  QueuedSerialTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
