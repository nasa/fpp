module Fw {

  @ Port for sending a request for a data product buffer to
  @ back a data product container
  port DpRequest(
      @ The container ID
      $id: FwDpIdType
      @ The size of the requested buffer
      $size: FwSizeType
  )

  @ Port for receiving a response to a buffer request
  port DpResponse(
      @ The container ID
      $id: FwDpIdType
      @ The buffer
      buffer: Fw.Buffer
      @ The status
      status: Fw.Success
  )

  @ Port for sending a data product buffer
  port DpSend(
      @ The container ID
      $id: FwDpIdType
      @ The buffer
      buffer: Fw.Buffer
  )

}
