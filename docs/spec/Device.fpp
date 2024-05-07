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

    initial junction j1 {
        go DeviceOff guard coldStart
        go DeviceOn action initPower
    }

    state DeviceOff {
        on PowerOn go DeviceOn action setPower
    }

    state DeviceOn {

        initial state Initializing {
            on Complete go Idle
        }

        state Idle {
            on Drive go Driving
            on Calibrate go Calibrating guard calibrateReady
        }

        state Calibrating {
            on RTI action doCalibrate
            on Fault go Idle action reportFault
            on Complete go Idle
        }

        state Driving {
            on RTI action motorControl
            on Stop go Idle
        }

        on POR go DeviceOn
        on Fault go j2
        on PowerOff go DeviceOff
    }

    junction j2 {
        go Diagnostics guard noRecovery
        go Recovery action reportFault
    }

    state Recovery {
        on RTI action doSafe
        on Complete go Diagnotics
    }

    state Diagnostics {
        on RTI action doDiagnostics
        on Resume go DeviceOn
    }

}
