// ======================================================================
// \title  QueuedCommandsTestMain.cpp
// \author [user name]
// \brief  cpp file for QueuedCommands component test main function
// ======================================================================

#include "QueuedCommandsTester.hpp"

TEST(Nominal, toDo) {
  QueuedCommandsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
