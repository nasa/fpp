;;; fpp-mode.el --- major mode for F Prime Prime -*- lexical-binding: t; -*-
;;
;; Author: Dennis Ogbe <do@ogbe.net>
;; Keywords: languages, fpp
;; Version: 1

;;; Commentary:
;;
;; Provides basic syntax highlighting and indentation for the FPP modeling
;; language (https://github.com/fprime-community/fpp)

;;; Code:

;; requires -------------------------------------------------------------------

;; we use `rx' for easier regexes
(eval-when-compile
  (require 'rx))

;; customizable variables -----------------------------------------------------

(defcustom fpp-mode-highlighted-symbols
  '("->" "$" "\\")
  "FPP symbols to highlight with `font-lock-constant-face'."
  :type '(list 'string)
  :group 'fpp)

(defcustom fpp-mode-basic-indent
  2
  "Basic number of spaces to indent with."
  :type 'natnum
  :group 'fpp)

;; constants ------------------------------------------------------------------

(defconst fpp-mode-definition-keywords
  '("type" "array" "component" "instance" "constant"
    "enum" "machine" "module" "port" "state" "struct" "topology")
  "FPP keywords for definitions.")

(defconst fpp-mode-keywords
  '("action" "active" "activity" "always"  "assert"
    "at" "base" "block" "change" "command"
    "connections" "cpu" "default" "diagnostic" "do"
    "drop" "else" "entry" "event" "exit"
    "false" "fatal" "format" "get" "guard"
    "guarded" "health" "high" "hook" "id" "if" "import"
    "include" "initial" "input" "internal" "junction"
    "locate" "low" "match"  "on"  "opcode" "orange"
    "output" "param" "passive" "phase"  "priority"
    "private" "queue"  "queued" "raw" "recv" "red"
    "ref" "reg" "resp" "save" "signal" "serial" "set" "severity"
    "size" "stack" "sync" "telemetry" "text" "throttle"
    "time" "true"  "update" "enter" "warning" "with"
    "yellow")
  "All non-definition keywords for FPP.")

(defconst fpp-mode-types
  '("F32" "F64" "I16" "I32" "I64" "I8"
    "U16" "U32" "U64" "U8" "bool" "string")
  "Built-in types for FPP.")

;; functions ------------------------------------------------------------------

(defun fpp-mode-inside-string (&optional ppss)
  "Return non-nil if inside string based on the syntax table.

   Call `syntax-ppss' if PPSS is not provided"
  (nth 3 (if ppss ppss (syntax-ppss))))

(defun fpp-mode-inside-comment (&optional ppss)
  "Return non-nil if inside comment based on the syntax table.

   Call `syntax-ppss' if PPSS is not provided"
  (nth 4 (if ppss ppss (syntax-ppss))))

(defun fpp-mode-not-in-comment-or-string ()
  "Return nil if inside a comment or a string."
  (not (or (fpp-mode-inside-comment)
           (fpp-mode-inside-string))))

(defun fpp-mode-re-search (direction re limit)
  "Perform search for RE in DIRECTION up until LIMIT.  Never raise error."
  (let ((fun (if (eq direction 'forward)
                 #'re-search-forward
               #'re-search-backward))
        (done) (ret))
    (while (not done)
      (setq ret (funcall fun re limit t))
      ;; disregard result if we are inside of a comment or string.
      (setq done (if ret (fpp-mode-not-in-comment-or-string) t)))
    ret))

(defun fpp-mode-is-continued-line (&optional arg)
  "Check whether we are on a continued line.  Move point when ARG is non-nil."
  (interactive)
  (let* ((beginning-of-prev-line (save-excursion
                                   (line-move -1 t)
                                   (point-at-bol)))
         (ret (if arg
                  (fpp-mode-re-search 'backward (rx "\\") beginning-of-prev-line)
                (save-excursion
                  (fpp-mode-re-search 'backward (rx "\\") beginning-of-prev-line)))))
    (when ret t)))

(defun fpp-mode-find-continued-line-begin ()
  "Move point to the beginning of a continued line."
  (interactive)
  (while (fpp-mode-is-continued-line t))
  (beginning-of-line))

(defun fpp-mode-indent-function ()
  "A very simple-minded indentation algorithm for FPP."
  (interactive)
  (let ((point-offset (- (current-column) (current-indentation)))
        (has-closing-paren
         (save-excursion
           (beginning-of-line)
           (fpp-mode-re-search 'forward (rx (or (intersection (not "{") "}")
                                                (intersection (not "(") ")")
                                                (intersection (not "[") "]")))
                               (point-at-eol)))))
    (if has-closing-paren
        ;; skip closing paren to get right number of levels from `syntax-ppss'
        (end-of-line)
      (beginning-of-line))
    (indent-line-to
       (cond ((fpp-mode-inside-string) ;; inside a string
              ;; ask the syntax parser for the beginning of the string
              ;; definition and indent smartly. if current indentation <
              ;; minimum, then add minimum to current. else do nothing
              (let ((minimum-indentation (save-excursion
                                           (goto-char (nth 8 (syntax-ppss)))
                                           (current-indentation)))
                    (curr-indentation (current-indentation)))
                (if (< curr-indentation minimum-indentation)
                    (min (+ curr-indentation minimum-indentation) minimum-indentation)
                  curr-indentation)))
             ((and (fpp-mode-is-continued-line) (not (fpp-mode-inside-comment (syntax-ppss))))
              ;; we are continuing a line, so add one unit of indentation to
              ;; the indentation of the first line.
              (let ((begin-level (save-excursion
                                   (fpp-mode-find-continued-line-begin)
                                   (max 0 (nth 0 (syntax-ppss)))))
                    ;; unless we encounter a single open brace, that is.
                    (single-open-brace (save-excursion
                                         (back-to-indentation)
                                         (looking-at "{"))))
                (* (if single-open-brace
                       begin-level
                     (1+ begin-level))
                   fpp-mode-basic-indent)))
             (t ;; normal case: not in string
              ;; ask the syntax parser about the current nesting level. indent
              ;; accordingly
              (* (max 0 (nth 0 (syntax-ppss))) fpp-mode-basic-indent))))
    ;; fix point relative to new indentation
    (when (>= point-offset 0)
      (move-to-column (+ (current-indentation) point-offset)))))

;; syntax table and propertize function ---------------------------------------

(defconst fpp-mode-syntax-table
      (let ((table (make-syntax-table)))
        ;; parens, brackets, etc
        (modify-syntax-entry ?\(  "()" table)
        (modify-syntax-entry ?\)  ")(" table)
        (modify-syntax-entry ?{ "(}" table)
        (modify-syntax-entry ?} "){" table)
        (modify-syntax-entry ?\[  "(]" table)
        (modify-syntax-entry ?\] ")[" table)
        ;; comments and annotations
        (modify-syntax-entry ?# "<" table)
        (modify-syntax-entry ?@ "<" table)
        (modify-syntax-entry ?\n ">" table)
        table)
      "The syntax table for FPP.")

;; since FPP allows triple-quoted multiline strings, we need to use the
;; `syntax-propertize-function' mechanism to properly set the `'syntax-table'
;; text property. this is the same problem as in python and other triple-quote
;; string languages. according to the documentatio, in order to identify a
;; multiline string, we need to set the `'syntax-table' property of the first
;; and last letters to "|". this is achieved by the following two functions.
;; acknowledging help from https://emacs.stackexchange.com/a/13383.

(defun fpp-mode-stringify-triple-quote ()
  "Put the correct `syntax-table' property on triple-quoted strings."
  (let* ((quotes-end (point))
         (quotes-start (- quotes-end 3))
         (ppss (syntax-ppss))
         (str-syntax (string-to-syntax "|")))
    (unless (fpp-mode-inside-comment ppss) ;; only apply if not inside of comment
      (if (fpp-mode-inside-string ppss)
          ;; we are in a string, so this must be the closing triple-quote. put
          ;; the | property (string fence) on the last " character
          (put-text-property (1- quotes-end) quotes-end 'syntax-table str-syntax)
        ;; we are NOT in a string, so this must be the opening triple-quote.
        ;; put the | property (string fence) on the first " character.
        (put-text-property quotes-start (1+ quotes-start) 'syntax-table str-syntax)))))

(defconst fpp-mode-syntax-propertize-function
  (syntax-propertize-rules
   ((rx "\"\"\"") (0 (ignore (fpp-mode-stringify-triple-quote)))))
  "Call `fpp-mode-stringyfy-triple-quote' when matching triple quotes.")

;; mode definition ------------------------------------------------------------

;;;###autoload
(define-derived-mode fpp-mode prog-mode "FPP"
  "Major mode for editing FPP source files."
  ;; search-based font-locking
  (set (make-local-variable 'font-lock-defaults)
       `(((,(rx-to-string `(and symbol-start (or ,@fpp-mode-keywords) symbol-end)) . font-lock-keyword-face)
          (,(rx-to-string `(and symbol-start (or ,@fpp-mode-definition-keywords) symbol-end)) . font-lock-keyword-face)
          (,(rx-to-string `(and symbol-start (or ,@fpp-mode-types) symbol-end)) . font-lock-type-face)
          (,(rx-to-string `(or ,@fpp-mode-highlighted-symbols)) . font-lock-constant-face))))
  ;; indentation
  (set (make-local-variable 'indent-line-function) #'fpp-mode-indent-function)
  ;; string triple quotes
  (set (make-local-variable 'syntax-propertize-function) fpp-mode-syntax-propertize-function)
  ;; comments
  (set (make-local-variable 'comment-start) "#")
  (set (make-local-variable 'comment-start-skip) "#+[\t ]*"))

;;;###autoload
(add-to-list 'auto-mode-alist '("\\.fpp\\'" . fpp-mode))

(provide 'fpp-mode)
;;; fpp-mode.el ends here
