constant a = 2 << 2
constant b = 8 >> 2
constant c = 5 << 0
constant d = 0 << 10
constant f = -4 << 1
constant g = -8 >> 1
enum E { X  = 2 } 
constant h = 4 << E.X
constant i = 4 << 2 << 3
constant j = (1 + 2) << 3
constant k = 1 + 2 >> 3 + 1
enum L: U32 { X = 1 << 31 }

constant SomeBytePatternOffset = 4
constant SomeBytePatternMask = 0x7F << SomeBytePatternOffset