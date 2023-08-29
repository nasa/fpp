# ======================================================================
# FPP file for data products configuration
# ======================================================================

module Fw {

  module DpCfg {

    @ The size in bytes of the user-configurable data in the container
    @ packet header
    constant CONTAINER_USER_DATA_SIZE = 32;

    @ The type of the identifier for the kind of processing to perform on
    @ a container before writing it to disk.
    enum ProcId : U8 {
      @ No processing
      NONE
    }

  }

}
