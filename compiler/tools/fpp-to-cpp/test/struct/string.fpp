struct String {
  s1: string
  s2: string size 40
  s0: string size 0
} default {s1 = "hello"}

struct StringArray {
  s1: string
  s2: [16] string size 40
  s0: [16] string size 0
}
