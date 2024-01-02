// ======================================================================
// \title  PassiveTelemetryTestMain.cpp
// \author [user name]
// \brief  cpp file for PassiveTelemetry component test main function
// ======================================================================

#include "PassiveTelemetryTester.hpp"

TEST(Nominal, toDo) {
  PassiveTelemetryTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
