;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; main()
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;_* Declarations =====================================================
(ns clj-libcrawl.core)

;;;_* Code =============================================================
(defn -main
  "Suggest friends for SCREEN-NAME."
  [screen-name]
  (try
    (clj-libcrawl.search/suggest
     (clj-libcrawl.twitter/screen-name->node screen-name))
    (catch Exception e (clojure.stacktrace/print-stack-trace e))))

;;;_* Emacs ============================================================
;;; Local Variables:
;;; allout-layout: t
;;; End:
