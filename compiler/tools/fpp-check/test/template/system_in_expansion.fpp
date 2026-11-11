# A system definition may not appear in a template expansion
module template T() {}

deployment topology D {}

expand T() {
  system S: D
}
