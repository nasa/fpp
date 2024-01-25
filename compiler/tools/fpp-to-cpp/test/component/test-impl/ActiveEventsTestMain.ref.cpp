// ======================================================================
// \title  ActiveEventsTestMain.cpp
// \author [user name]
// \brief  cpp file for ActiveEvents component test main function
// ======================================================================

#include "ActiveEventsTester.hpp"

TEST(Nominal, toDo) {
  ActiveEventsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
