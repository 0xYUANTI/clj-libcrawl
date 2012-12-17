;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; main()
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;_* Declarations =====================================================
(ns clj-libcrawl.core
  (:require [clj-libcrawl.search  :as search])
  (:require [clj-libcrawl.twitter :as twitter])
  (:require [clj-libcrawl.http    :as http])
  )

;;;_* Code =============================================================
(defn -main
  "Suggest friends for SCREEN-NAME."
  [screen-name]
  (try
    (search/suggest (twitter/screen-name->node screen-name))
    (future-cancel http/bgtask)
    (System/exit 0)
    (catch Exception e (clojure.stacktrace/print-stack-trace e))))

;;;_* Emacs ============================================================
;;; Local Variables:
;;; allout-layout: t
;;; End:
