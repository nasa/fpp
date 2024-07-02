
struct FaultData {
    $id: U32
    data: U32
}

struct PowerData {
    level: F32
}

# Start of the state machine definition
state machine Device {

# Specify state machine events
    signal RTI
    signal Complete
    signal Calibrate
    signal Fault: FaultData
    signal Drive
    signal Stop
    signal Resume
    signal POR
    signal PowerOn: PowerData
    signal PowerOff

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
    initial do init1 enter j1

    junction j1 {
        if coldStart enter DEVICE_OFF \
        else do initPower enter DEVICE_ON
    }

    state DEVICE_OFF {
        on PowerOn do setPower enter DEVICE_ON
    }

    state DEVICE_ON {

        initial do init2 enter INITIALIZING

        state INITIALIZING {
            on Complete enter IDLE
        }

        state IDLE {
            on Drive enter DRIVING
            on Calibrate if calibrateReady enter CALIBRATING
        }

        state Calibrating {
            on RTI do doCalibrate
            on Fault do reportFault enter IDLE
            on Complete enter IDLE
        }

        state DRIVING {
            on RTI do motorControl
            on Stop enter IDLE
        }

        on POR enter DEVICE_ON
        on Fault enter j2
        on PowerOff enter DEVICE_OFF
    }

    junction j2 {
        if noRecovery enter DIAGNOSTICS \
        else do reportFault enter RECOVERY
    }

    state Recovery {
        on RTI do doSafe
        on Complete enter DIAGNOSTICS
    }

    state DIAGNOSTICS {
        on RTI do doDiagnostics
        on Resume enter DEVICE_ON
    }

}
