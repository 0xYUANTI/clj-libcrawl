;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Miscellaneous functions.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;_* Declarations =====================================================
(ns clj-libcrawl.lib)

;;;_* Code =============================================================
(defn member?
  "Returns true iff X is an element of XS."
  [x xs]
  (some #(= x %) xs))

(defn flatmap
  "Map F over XS and flatten the result."
  [f xs]
  (flatten (map f xs)))

;;;_* Emacs ============================================================
;;; Local Variables:
;;; allout-layout: t
;;; End:
