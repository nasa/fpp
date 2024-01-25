// ======================================================================
// \title  QueuedEventsTestMain.cpp
// \author [user name]
// \brief  cpp file for QueuedEvents component test main function
// ======================================================================

#include "QueuedEventsTester.hpp"

TEST(Nominal, toDo) {
  QueuedEventsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
