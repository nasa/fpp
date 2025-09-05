constant a = 1234
constant b = 0xABCD

constant c = "This is a string."

constant d = [ 1, 2, 3 ]
constant e = [ 1, 2.0 ]
constant f = [ [ 1, 2 ], [ 3, 4 ] ]

constant g = f[0]
constant h = f[0][1]

constant i = { x = 1, y = "abc", z = false }
constant j = {}

constant k = [ { x = 1, y = 2 }, { x = 3 } ]

constant l = 1
constant m = l

constant n = -1
constant o = 1 + 2
constant p = (1)

constant q = true
constant r = false

@ This is a pre annotation.
@ It has two lines.
constant s = 0 @< This is a post annotation.
               @< It also has two lines.
