// ======================================================================
// \title  QueuedExternalParamsTestMain.cpp
// \author [user name]
// \brief  cpp file for QueuedExternalParams component test main function
// ======================================================================

#include "QueuedExternalParamsTester.hpp"

TEST(Nominal, toDo) {
  QueuedExternalParamsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
