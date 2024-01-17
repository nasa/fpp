// ======================================================================
// \title  QueuedGuardedProductsTestMain.cpp
// \author [user name]
// \brief  cpp file for QueuedGuardedProducts component test main function
// ======================================================================

#include "QueuedGuardedProductsTester.hpp"

TEST(Nominal, toDo) {
  QueuedGuardedProductsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
