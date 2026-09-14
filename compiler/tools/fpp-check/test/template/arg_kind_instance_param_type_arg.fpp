port P

interface I {
    sync input port pIn: P
}

module template T(instance i: I) {
    topology Top {
        instance i
    }
}

expand T(type U32)
