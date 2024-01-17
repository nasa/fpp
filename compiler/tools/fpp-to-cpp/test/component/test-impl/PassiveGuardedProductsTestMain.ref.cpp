// ======================================================================
// \title  PassiveGuardedProductsTestMain.cpp
// \author [user name]
// \brief  cpp file for PassiveGuardedProducts component test main function
// ======================================================================

#include "PassiveGuardedProductsTester.hpp"

TEST(Nominal, toDo) {
  PassiveGuardedProductsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
