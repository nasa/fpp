passive component Time {
    sync input port timeGetPort: Fw.Time
}

passive component Command {
    sync input port cmdIn: Fw.Cmd
    sync input port cmdRegOut: Fw.CmdReg
    sync input port cmdResponseOut: Fw.CmdResponse
    output port cmdSend: Fw.Cmd
}

passive component Event {
    sync input port eventLog: Fw.Log
}

passive component Health {
    output port PingSend: Svc.Ping
    sync input port PingReturn: Svc.Ping
}

passive component Param {
    sync input port paramGet: Fw.PrmGet
    sync input port paramSet: Fw.PrmSet
}

passive component Telemetry {
    sync input port tlm: Fw.Tlm
}

passive component TextEvent {
    sync input port textEventLog: Fw.LogText
}

instance c1: Time base id 0x100
instance c2: Command base id 0x200
instance c3: Event base id 0x300
instance c4: Health base id 0x400
instance c5: Param base id 0x500
instance c6: Telemetry base id 0x600
instance c7: TextEvent base id 0x700

topology T {
    instance c1
    instance c2
    instance c3
    instance c4
    instance c5
    instance c6
    instance c7

    time connections instance c1
    command connections instance c2
    event connections instance c3
    health connections instance c4
    param connections instance c5
    telemetry connections instance c6
    text event connections instance c7
}