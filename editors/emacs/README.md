# editors/emacs

This directory contains an Emacs major mode for FPP. The mode provides basic
syntax highlighting and indentation for FPP source files.

# Installation / use

First, copy `fpp-mode.el` somewhere into your `.emacs.d` directory or whereever
you keep your packages.

## Plain `.emacs`

```emacs-lisp
(add-to-list 'load-path "path/to/fpp-mode.el")
(require 'fpp-mode)
```

## [`use-package`](https://github.com/jwiegley/use-package)

```emacs-lisp
(use-package fpp-mode
  :load-path "path/to/fpp-mode.el")
```
