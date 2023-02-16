module FppTest {

  @ A component for testing  data product code gen
  active component DpTest {

    # ---------------------------------------------------------------------- 
    # Constants and type
    # ---------------------------------------------------------------------- 

    @ Data for a DataRecord
    struct Data {
      @ A U16 field
      u16Field: U16
    }

    # ----------------------------------------------------------------------
    # Special ports
    # ----------------------------------------------------------------------

    @ Data product request port
    product request port productRequestOut

    @ Data product receive port
    async product recv port productRecvIn

    @ Data product send port
    product send port productSendOut

    @ Time get port
    time get port timeGetOut

    # ---------------------------------------------------------------------- 
    # General ports
    # ---------------------------------------------------------------------- 

    @ A schedIn port to run the data product generation
    async input port schedIn: Svc.Sched

    # ---------------------------------------------------------------------- 
    # Records
    # ---------------------------------------------------------------------- 

    @ Record 1
    product record U32Record: U32 id 100

    @ Record 2
    product record DataRecord: Data id 200

    # ----------------------------------------------------------------------
    # Containers
    # ----------------------------------------------------------------------

    @ Container 1
    product container Container1 id 300 default priority 10

    @ Container 2
    product container Container2 id 400 default priority 20

  }

}
