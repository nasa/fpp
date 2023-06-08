module Fw {

  port Cmd

}

passive component C {

  sync command recv port cmdIn

}

