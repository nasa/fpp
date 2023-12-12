// ======================================================================
// \title  EmptyTestMain.cpp
// \author [user name]
// \brief  cpp file for Empty component test main function
// ======================================================================

#include "EmptyTester.hpp"

TEST(Nominal, toDo) {
  EmptyTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
