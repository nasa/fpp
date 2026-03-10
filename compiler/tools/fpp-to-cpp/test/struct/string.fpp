struct String {
  s1: string
  s2: string size 40
} default {s1 = "hello"}

struct StringArray {
  s1: string
  s2: [16] string size 40
}
