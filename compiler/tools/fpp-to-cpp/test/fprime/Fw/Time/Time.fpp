module Fw {

  type Time

  @ Time port
  port Time(
             ref $time: Fw.Time @< The time tag
           )

}
