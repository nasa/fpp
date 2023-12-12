// ======================================================================
// \title  PassiveEventsTestMain.cpp
// \author [user name]
// \brief  cpp file for PassiveEvents component test main function
// ======================================================================

#include "PassiveEventsTester.hpp"

TEST(Nominal, toDo) {
  PassiveEventsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
