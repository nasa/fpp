# This file defines a template whose body uses splitConst.
# The template is expanded in template_body_locate_split_b.fpp.
locate constant splitConst at "template_body_locate_split_dep.fpp"

module template BodyLocSplitT(constant p: U32) {
  constant cOut = p + splitConst
}
