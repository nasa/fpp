// ======================================================================
// \title  PassiveSyncProductsTestMain.cpp
// \author [user name]
// \brief  cpp file for PassiveSyncProducts component test main function
// ======================================================================

#include "PassiveSyncProductsTester.hpp"

TEST(Nominal, toDo) {
  PassiveSyncProductsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
