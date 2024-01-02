// ======================================================================
// \title  QueuedTelemetryTestMain.cpp
// \author [user name]
// \brief  cpp file for QueuedTelemetry component test main function
// ======================================================================

#include "QueuedTelemetryTester.hpp"

TEST(Nominal, toDo) {
  QueuedTelemetryTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
