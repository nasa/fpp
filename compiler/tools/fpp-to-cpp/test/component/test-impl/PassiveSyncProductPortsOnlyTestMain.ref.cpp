// ======================================================================
// \title  PassiveSyncProductPortsOnlyTestMain.cpp
// \author [user name]
// \brief  cpp file for PassiveSyncProductPortsOnly component test main function
// ======================================================================

#include "PassiveSyncProductPortsOnlyTester.hpp"

TEST(Nominal, toDo) {
  PassiveSyncProductPortsOnlyTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
