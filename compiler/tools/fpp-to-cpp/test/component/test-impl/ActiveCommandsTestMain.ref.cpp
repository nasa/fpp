// ======================================================================
// \title  ActiveCommandsTestMain.cpp
// \author [user name]
// \brief  cpp file for ActiveCommands component test main function
// ======================================================================

#include "ActiveCommandsTester.hpp"

TEST(Nominal, toDo) {
  ActiveCommandsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
