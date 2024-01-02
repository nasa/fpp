// ======================================================================
// \title  QueuedSyncProductsTestMain.cpp
// \author [user name]
// \brief  cpp file for QueuedSyncProducts component test main function
// ======================================================================

#include "QueuedSyncProductsTester.hpp"

TEST(Nominal, toDo) {
  QueuedSyncProductsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
