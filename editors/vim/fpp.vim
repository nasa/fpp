" Vim syntax file
" Language:    fpp
" Maintainer:  Robert Bocchino <bocchino@jpl.nasa.gov>
" Last Change: 8 October 2019

" Indent words
"
setl cinwords=enum,namespace,struct
setl cindent

" keywords
syn keyword fppKeyword active
syn keyword fppKeyword array
syn keyword fppKeyword assert
syn keyword fppKeyword async
syn keyword fppKeyword at
syn keyword fppKeyword baseid
syn keyword fppKeyword block
syn keyword fppKeyword command
syn keyword fppKeyword component
syn keyword fppKeyword constant
syn keyword fppKeyword default
syn keyword fppKeyword diagnostic
syn keyword fppKeyword drop
syn keyword fppKeyword enum
syn keyword fppKeyword event
syn keyword fppKeyword false
syn keyword fppKeyword format
syn keyword fppKeyword get
syn keyword fppKeyword guarded
syn keyword fppKeyword import
syn keyword fppKeyword input
syn keyword fppKeyword instance
syn keyword fppKeyword internal
syn keyword fppKeyword locate
syn keyword fppKeyword module
syn keyword fppKeyword output
syn keyword fppKeyword param
syn keyword fppKeyword passive
syn keyword fppKeyword port
syn keyword fppKeyword priority
syn keyword fppKeyword queued
syn keyword fppKeyword recv
syn keyword fppKeyword ref
syn keyword fppKeyword reg
syn keyword fppKeyword resp
syn keyword fppKeyword set
syn keyword fppKeyword severity
syn keyword fppKeyword size
syn keyword fppKeyword struct
syn keyword fppKeyword sync
syn keyword fppKeyword telemetry
syn keyword fppKeyword text
syn keyword fppKeyword time
syn keyword fppKeyword true
syn keyword fppKeyword type

syn match fppOperator      /\//

" types
syn keyword fppType        F32
syn keyword fppType        F64
syn keyword fppType        I16
syn keyword fppType        I32
syn keyword fppType        I64
syn keyword fppType        I8
syn keyword fppType        U16
syn keyword fppType        U32
syn keyword fppType        U64
syn keyword fppType        U8
syn keyword fppType        bool
syn keyword fppType        string

" comments
syn match fppComment       /#.*$/
syn match fppAnnotation    /@.*$/
syn match fppTodo          /\v<TODO/ containedin=ALL

" numbers
syn match fppNumber /\v<\d+(\.\d*)?([Ee]-?\d+)?/
syn match fppNumber /\.\d+([Ee]-?\d+)?/
syn match fppNumber /\v<0x\x+/

" strings
:syntax region String matchgroup=String start=+"""+  skip=+\\"+  end=+"""+
:syntax region String matchgroup=String start=+"+  skip=+\\"+  end=+"+

hi def link fppAnnotation  Special
hi def link fppComment     Comment
hi def link fppKeyword     Keyword
hi def link fppNumber      Number
hi def link fppOperator    Operator
hi def link fppString      String
hi def link fppTodo        Todo
hi def link fppType        Type
