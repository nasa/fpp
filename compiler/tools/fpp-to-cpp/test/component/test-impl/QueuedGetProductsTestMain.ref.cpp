// ======================================================================
// \title  QueuedGetProductsTestMain.cpp
// \author [user name]
// \brief  cpp file for QueuedGetProducts component test main function
// ======================================================================

#include "QueuedGetProductsTester.hpp"

TEST(Nominal, toDo) {
  QueuedGetProductsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
