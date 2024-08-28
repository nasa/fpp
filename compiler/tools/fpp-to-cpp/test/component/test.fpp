module M {

  state machine DeviceSm
  state machine HackSm

  @ An active component
  active component ActiveTest {

    state machine instance device1: DeviceSm priority 1 drop
    state machine instance device2: DeviceSm priority 2 assert
    state machine instance device3: DeviceSm priority 3 block
    state machine instance device4: HackSm priority 4 hook
    state machine instance device5: HackSm
  }

}
