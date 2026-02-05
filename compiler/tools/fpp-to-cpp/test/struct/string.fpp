constant FW_FIXED_LENGTH_STRING_SIZE = 60

struct String {
  s1: string
  s2: string size 40
} default {s1 = "hello"}

struct StringArray {
  s1: string
  s2: [16] string size 40
}
