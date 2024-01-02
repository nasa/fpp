// ======================================================================
// \title  PassiveCommandsTestMain.cpp
// \author [user name]
// \brief  cpp file for PassiveCommands component test main function
// ======================================================================

#include "PassiveCommandsTester.hpp"

TEST(Nominal, toDo) {
  PassiveCommandsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
