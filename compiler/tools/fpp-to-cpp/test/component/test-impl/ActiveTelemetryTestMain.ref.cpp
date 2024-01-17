// ======================================================================
// \title  ActiveTelemetryTestMain.cpp
// \author [user name]
// \brief  cpp file for ActiveTelemetry component test main function
// ======================================================================

#include "ActiveTelemetryTester.hpp"

TEST(Nominal, toDo) {
  ActiveTelemetryTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
