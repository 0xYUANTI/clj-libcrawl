;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Interface to Twitter's social graph.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;_* Declarations =====================================================
(ns clj-libcrawl.twitter
  (:require [clj-libcrawl.graph :as graph])
  (:require [clj-libcrawl.http  :as http])
  (:require [clj-libcrawl.lib   :as lib])
  (:require [clojure.string     :as string])
  (:use     clojure.test))

(declare friends-ids ids->nodes users-lookup user->node uri)

;;;_* Code =============================================================
;;;_ * API -------------------------------------------------------------
(defrecord TwitterNode [id name]
  graph/Node
  (user         [_] name)
  (predecessors [_] (throw (Exception. "nyi")))
  (successors   [_] (ids->nodes (friends-ids name))))

(deftest twitternode-test
  (is (lib/member? "IGLevine"
                   (map :name
                        (graph/successors
                         (TwitterNode. 162704550 "cannedprimates"))))))


(defn screen-name->node
  "Construct a TwitterNode from a Twitter handle."
  [screen-name]
  (TwitterNode.
   (:id (http/get (uri "users/show.json") {"screen_name" screen-name}))
   screen-name))

(deftest screen-name->node-test
  (let [node (screen-name->node "cannedprimates")]
    (is (= (:id   node) 162704550))
    (is (= (:name node) "cannedprimates"))))

;;;_ * Internals -------------------------------------------------------
(defn- friends-ids [screen-name]
  (:ids (http/get (uri "friends/ids.json") {"screen_name" screen-name})))

(defn- ids->nodes [ids] (map user->node (users-lookup ids)))

(defn- users-lookup
  [ids]
  (flatten
   (http/multiget
    (map (fn [ids]
           [(uri "users/lookup.json") {"user_id" (string/join "," ids)}])
         (partition-all 100 ids)))))

(defn- user->node [user] (->TwitterNode (:id user) (:screen_name user)))

(defn- uri [resource] (str "https://api.twitter.com/1/" resource))

;;;_* Emacs ============================================================
;;; Local Variables:
;;; allout-layout: t
;;; End:
