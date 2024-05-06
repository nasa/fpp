# Define a basic state machine event type
# Only contains the required state machine ID and an event signal,
# no payload data
struct SMEvents {
    smId : U32
    eventSignal : U32
}

# Start of the state machine definition
state machine Device {

    event RTI: SMEvents
    event Complete: SMEvents
    event Calibrate: SMEvents
    event Fault: SMEvents
    event Drive: SMEvents
    event Stop: SMEvents
    event Resume: SMEvents
    event POR: SMEvents

    state Off

    state On {

        state Initializing

        state Idle

        state Calibrating {

            guard doCalibrate: SMEvents
            internal event RTI
                     guard doCalibrate
        }

        state Driving {

            action motorControl: SMEvents
            internal event RTI
                     action motorControl
        }

        [*] -> Initializing

        Initializing -> Idle event Complete

        Idle -> Driving event Drive

        guard calibrateReady: SMEvents
        Idle -> Calibrating event Calibrate
                            guard calibrateReady

        action reportFault: SMEvents
        Calibrating -> Idle event Fault
                            action reportFault

        Calibrating -> Idle event Complete

        Driving -> Idle event Stop
    }

    state Recovery {

        action doSafe: SMEvents
        internal event RTI
                  action doSafe
    }

    state Diagnostics {

        action doDiagnostics: SMEvents
        internal event RTI
                 action: doDiagnostics
    }
 
    [*] -> Off
 
    Off -> On event PowerOn

    On -> Off event PowerOff

    action reportFault: SMEvents
    On -> Recovery event Fault
                   action reportFault

    Diagnostics -> On event Resume

    Recovery -> Diagnostics event Complete

    On -> On event POR
}
