// ======================================================================
// \title  PassiveSerialTestMain.cpp
// \author [user name]
// \brief  cpp file for PassiveSerial component test main function
// ======================================================================

#include "PassiveSerialTester.hpp"

TEST(Nominal, toDo) {
  PassiveSerialTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
