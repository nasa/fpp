passive component C {

}

instance c1: C base id 0x100
instance c2: C base id 0x200

init c1 phase 0 \
"""
c1 phase 0
"""
init c1 phase 1 \
"""
c1 phase 1
"""
init c2 phase 0 \
"""
c2 phase 0
"""
init c2 phase 1 \
"""
c2 phase 1
"""
