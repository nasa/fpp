# A deployment topology may not appear in a template expansion
module template T() {}

expand T() {
  deployment topology D {}
}
