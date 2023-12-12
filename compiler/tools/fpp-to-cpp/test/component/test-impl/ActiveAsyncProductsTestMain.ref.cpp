// ======================================================================
// \title  ActiveAsyncProductsTestMain.cpp
// \author [user name]
// \brief  cpp file for ActiveAsyncProducts component test main function
// ======================================================================

#include "ActiveAsyncProductsTester.hpp"

TEST(Nominal, toDo) {
  ActiveAsyncProductsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
