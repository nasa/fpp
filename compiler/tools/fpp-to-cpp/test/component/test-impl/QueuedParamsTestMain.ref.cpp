// ======================================================================
// \title  QueuedParamsTestMain.cpp
// \author [user name]
// \brief  cpp file for QueuedParams component test main function
// ======================================================================

#include "QueuedParamsTester.hpp"

TEST(Nominal, toDo) {
  QueuedParamsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
