# Define the data structure for events that carry data
struct FaultData {
    id: U32
    data: U32
}

struct PowerData {
    level: F32
}

# Start of the state machine definition
state machine Device {

# Specify state machine events
    event RTI
    event Complete
    event Calibrate
    event Fault: FaultData
    event Drive
    event Stop
    event Resume
    event POR
    event PowerOn: PowerData
    event PowerOff

# Specify actions
    action init1
    action init2
    action setPower: PowerData
    action initPower
    action reportFault: FaultData
    action motorControl
    action doCalibrate
    action doSafe
    action doDiagnostics

# Specify guards
    guard coldStart
    guard noRecovery: FaultData
    guard calibrateReady

# Specify states and junctions
    initial j1 do init1

    junction j1 {
        if coldStart visit DeviceOff
        else visit DeviceOn do initPower
    }

    state DeviceOff {
        on PowerOn visit DeviceOn do setPower
    }

    state DeviceOn {

        initial Initializing do init2

        state Initializing {
            on Complete visit Idle
        }

        state Idle {
            on Drive visit Driving
            on Calibrate if calibrateReady visit Calibrating
        }

        state Calibrating {
            on RTI do doCalibrate
            on Fault visit Idle do reportFault
            on Complete visit Idle
        }

        state Driving {
            on RTI do motorControl
            on Stop visit Idle
        }

        on POR visit DeviceOn
        on Fault visit j2
        on PowerOff visit DeviceOff
    }

    junction j2 {
        if noRecovery visit Diagnostics
        else visit Recovery do reportFault
    }

    state Recovery {
        on RTI do doSafe
        on Complete visit Diagnotics
    }

    state Diagnostics {
        on RTI do doDiagnostics
        on Resume visit DeviceOn
    }

}
