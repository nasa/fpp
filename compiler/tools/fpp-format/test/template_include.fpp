include "template_include.fppi"

@ Expanding a template defined in an included file requires -t to also
@ resolve the include so the template definition can be found
expand Included(constant 42)
