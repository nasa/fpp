" Vim syntax file
" Language:    fpp
" Maintainer:  Robert Bocchino <bocchino@jpl.nasa.gov>
" Last Change: 8 October 2019

" Indent words
"
setl cinwords=enum,namespace,struct
setl cindent

" keywords
syn keyword fppKeyword at
syn keyword fppKeyword array
syn keyword fppKeyword component
syn keyword fppKeyword constant
syn keyword fppKeyword default
syn keyword fppKeyword enum
syn keyword fppKeyword false
syn keyword fppKeyword locate
syn keyword fppKeyword module
syn keyword fppKeyword port
syn keyword fppKeyword struct
syn keyword fppKeyword true
syn keyword fppKeyword type

syn match fppOperator      /-/
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
syn match fppNumber /\v<\.\d+([Ee]-?\d+)?/
syn match fppNumber /\v<0x\x+/

hi def link fppAnnotation  Special
hi def link fppComment     Comment
hi def link fppKeyword     Keyword
hi def link fppNumber      Number
hi def link fppOperator    Operator
hi def link fppTodo        Todo
hi def link fppType        Type
