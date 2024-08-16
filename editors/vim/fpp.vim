" Vim syntax file
" Language:    fpp
" Maintainer:  Robert Bocchino <bocchino@jpl.nasa.gov>

" Indent words
"
setl cinwords=component,enum,module,struct,topology
setl cindent

" keywords
syn keyword fppKeyword action
syn keyword fppKeyword active
syn keyword fppKeyword activity
syn keyword fppKeyword always
syn keyword fppKeyword array
syn keyword fppKeyword assert
syn keyword fppKeyword async
syn keyword fppKeyword at
syn keyword fppKeyword base
syn keyword fppKeyword block
syn keyword fppKeyword change
syn keyword fppKeyword command
syn keyword fppKeyword component
syn keyword fppKeyword connections
syn keyword fppKeyword constant
syn keyword fppKeyword container
syn keyword fppKeyword cpu
syn keyword fppKeyword default
syn keyword fppKeyword diagnostic
syn keyword fppKeyword do
syn keyword fppKeyword drop
syn keyword fppKeyword else
syn keyword fppKeyword entry
syn keyword fppKeyword enter
syn keyword fppKeyword enum
syn keyword fppKeyword event
syn keyword fppKeyword exit
syn keyword fppKeyword false
syn keyword fppKeyword fatal
syn keyword fppKeyword format
syn keyword fppKeyword get
syn keyword fppKeyword guard
syn keyword fppKeyword guarded
syn keyword fppKeyword health
syn keyword fppKeyword high
syn keyword fppKeyword hook
syn keyword fppKeyword id
syn keyword fppKeyword if
syn keyword fppKeyword import
syn keyword fppKeyword include
syn keyword fppKeyword initial
syn keyword fppKeyword input
syn keyword fppKeyword instance
syn keyword fppKeyword internal
syn keyword fppKeyword junction
syn keyword fppKeyword locate
syn keyword fppKeyword low
syn keyword fppKeyword machine
syn keyword fppKeyword match
syn keyword fppKeyword module
syn keyword fppKeyword on
syn keyword fppKeyword opcode
syn keyword fppKeyword orange
syn keyword fppKeyword output
syn keyword fppKeyword param
syn keyword fppKeyword passive
syn keyword fppKeyword phase
syn keyword fppKeyword port
syn keyword fppKeyword priority
syn keyword fppKeyword private
syn keyword fppKeyword product
syn keyword fppKeyword queue
syn keyword fppKeyword queued
syn keyword fppKeyword record
syn keyword fppKeyword recv
syn keyword fppKeyword red
syn keyword fppKeyword ref
syn keyword fppKeyword reg
syn keyword fppKeyword request
syn keyword fppKeyword resp
syn keyword fppKeyword save
syn keyword fppKeyword send
syn keyword fppKeyword serial
syn keyword fppKeyword set
syn keyword fppKeyword severity
syn keyword fppKeyword signal
syn keyword fppKeyword size
syn keyword fppKeyword stack
syn keyword fppKeyword state
syn keyword fppKeyword struct
syn keyword fppKeyword sync
syn keyword fppKeyword telemetry
syn keyword fppKeyword text
syn keyword fppKeyword throttle
syn keyword fppKeyword time
syn keyword fppKeyword topology
syn keyword fppKeyword true
syn keyword fppKeyword type
syn keyword fppKeyword update
syn keyword fppKeyword warning
syn keyword fppKeyword with
syn keyword fppKeyword yellow

syn match fppOperator      /\\/

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
syn match fppNumber /\v<0[xX]\x+/

" identifiers
syn match fppIdentifier    /\v\$?[_A-Za-z][_A-Za-z0-9]*/

" strings
:syntax region String matchgroup=String start=+"+ skip=/\\./ end=+"+
:syntax region String matchgroup=String start=+"""+ skip=/\(\\.\|"[^"]\|""[^"]\)/ end=+"""+

hi def link fppAnnotation  Special
hi def link fppComment     Comment
hi def link fppIdentifier  Identifier
hi def link fppKeyword     Keyword
hi def link fppNumber      Number
hi def link fppOperator    Operator
hi def link fppString      String
hi def link fppTodo        Todo
hi def link fppType        Type
