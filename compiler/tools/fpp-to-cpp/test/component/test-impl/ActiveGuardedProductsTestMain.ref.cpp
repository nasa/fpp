// ======================================================================
// \title  ActiveGuardedProductsTestMain.cpp
// \author [user name]
// \brief  cpp file for ActiveGuardedProducts component test main function
// ======================================================================

#include "ActiveGuardedProductsTester.hpp"

TEST(Nominal, toDo) {
  ActiveGuardedProductsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
