// ======================================================================
// \title  PassiveGetProductsTestMain.cpp
// \author [user name]
// \brief  cpp file for PassiveGetProducts component test main function
// ======================================================================

#include "PassiveGetProductsTester.hpp"

TEST(Nominal, toDo) {
  PassiveGetProductsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
