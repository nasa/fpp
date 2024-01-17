// ======================================================================
// \title  ActiveSyncProductsTestMain.cpp
// \author [user name]
// \brief  cpp file for ActiveSyncProducts component test main function
// ======================================================================

#include "ActiveSyncProductsTester.hpp"

TEST(Nominal, toDo) {
  ActiveSyncProductsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
