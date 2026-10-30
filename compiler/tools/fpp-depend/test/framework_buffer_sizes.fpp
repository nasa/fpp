locate constant FW_CMD_ARG_BUFFER_MAX_SIZE at "cmd_arg_buffer.fpp"
locate constant FW_LOG_BUFFER_MAX_SIZE at "log_buffer.fpp"
locate constant FW_PARAM_BUFFER_MAX_SIZE at "param_buffer.fpp"
locate constant FW_TLM_BUFFER_MAX_SIZE at "tlm_buffer.fpp"

active component C {

  sync command C

  event E severity activity low format ""

  param P: U32

  telemetry T: U32

}
