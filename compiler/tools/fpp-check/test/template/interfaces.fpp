interface I {

}

template T(interface i: I) {
    topology P {
        instance i
    }
}

passive component C {}

instance c: C base id 1

expand T(c)
