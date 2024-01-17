// ======================================================================
// \title  ActiveGetProductsTestMain.cpp
// \author [user name]
// \brief  cpp file for ActiveGetProducts component test main function
// ======================================================================

#include "ActiveGetProductsTester.hpp"

TEST(Nominal, toDo) {
  ActiveGetProductsTester tester;
  tester.toDo();
}

int main(int argc, char** argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
