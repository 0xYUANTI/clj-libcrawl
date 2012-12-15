;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Graph traversal.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;_* Declarations =====================================================
(ns clj-libcrawl.graph
  (:require [clj-libcrawl.lib :as lib])
  (:use     clojure.test))

(declare neighbors-loop)

;;;_* Code =============================================================
(defprotocol Node
  "A node in a social graph."
  (user         [node] "Return the user associated with NODE.")
  (predecessors [node] "Return the predecessor set of NODE.")
  (successors   [node] "Return the successor set of NODE."))


(defn traverse
  "Return the nodes visited during a breadth-first traversal from ROOT."
  ([root]
     (let [succ (successors root)]
       (traverse (first succ) (rest succ) #{(user root)})))
  ([node todo seen]
     (lazy-seq
      (if (not (get seen (user node)))
        (cons node
              (traverse (first todo)
                        (concat (rest todo) (successors node)) ;assume infinite
                        (conj seen (user node))))
        (traverse (first todo)
                  (rest todo)
                  seen)))))


(defn neighbors
  "Return the nodes at distance N from ROOT."
  ([root]
     (neighbors root 1))
  ([root n]
     (assert (> n 0))
     (neighbors-loop (list root) n)))

(defn- neighbors-loop [nodes n]
  (if (= n 1)
    (lib/flatmap successors nodes)
    (recur (lib/flatmap successors nodes) (dec n))))

;;;_* Tests ============================================================
(defrecord TestNode [x]
  Node
  (user         [_] x)
  (predecessors [_] ())
  (successors   [_] (map ->TestNode (list x (+ x 1) (+ x 2)))))

(deftest traverse-test
  (is (= '(2 3 4 5 6 7 8 9 10 11)
         (map user (take 10 (traverse (TestNode. 1)))))))

(deftest neighbors-test
  (is (= '(1 2 3 2 3 4 3 4 5)
         (map user (neighbors (TestNode. 1) 2)))))

;;;_* Emacs ============================================================
;;; Local Variables:
;;; allout-layout: t
;;; End:
