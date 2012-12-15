;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Trivial search engine.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;_* Declarations =====================================================
(ns clj-libcrawl.search
  (:require [clj-libcrawl.graph :as graph])
  (:use     clojure.test))

(declare neighbors rank display)

;;;_* Code =============================================================
(defn suggest
  "Suggest users for USER to follow based on how many users USER follows
   follow a given user USER doesn't follow."
  [user]
  (display (rank (neighbors user 2)) (set (neighbors user 1))))

(defn- neighbors [root n] (map graph/user (graph/neighbors root n)))

(defn- rank [users] (sort-by val > (frequencies users)))

(defn- display [suggestions following]
  (doseq [[user followers] suggestions]
    (if (get following user)
      (printf "(%s: %s)\n" user followers)
      (printf "%s: %s\n"   user followers))))

;;;_* Tests ============================================================
(deftest suggest-test
  (is (= "(3: 3)\n(2: 2)\n4: 2\n(1: 1)\n5: 1\n"
         (with-out-str (suggest (graph/->TestNode 1))))))

;;;_* Emacs ============================================================
;;; Local Variables:
;;; allout-layout: t
;;; End:
