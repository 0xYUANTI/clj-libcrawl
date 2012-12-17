;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Utilities for consuming HTTP APIs.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;_* Declarations =====================================================
(ns clj-libcrawl.http
  (:require [clj-libcrawl.lib :as lib])
  (:require [clj-http.client  :as client])
  (:refer-clojure :exclude (get))
  (:use     clojure.test))

(declare token update! update-window! timestamp)

;;;_* Code =============================================================
;;;_ * API -------------------------------------------------------------
(defn get
  "GET the resource at URI as JSON."
  ([uri]
     (get uri {}))
  ([uri params]
     (token) ;rate limiting
     (try
       (:body (client/get uri {:query-params params :as :json}))
       (catch Exception e
         (println "exn: " e)
         {}))))

(deftest get-test
  (let [{ids :ids} (get "https://api.twitter.com/1/friends/ids.json"
                        {"screen_name" "cannedprimates"})]
    (is (lib/member? 64488804 ids))))


(defn multiget
  "GET multiple JSON resources in parallel."
  ([reqs] (map deref (map #(future (apply get %)) reqs))))

(deftest multiget-test
  (let [uri
        "https://api.twitter.com/1/users/lookup.json"

        [[{n1 :screen_name}] [{n2 :screen_name}]]
        (multiget (list [uri {"user_id" "64488804"}]
                        [uri {"user_id" "11420652"}]))]

    (is (= "IGLevine"   n1))
    (is (= "Harvey1966" n2))))

;;;_ * Internals -------------------------------------------------------
;; Twitter limits us to 150 reqs/hour.
;; We go for 100 to be on the safe side (since Twitter counts requests
;; by IP and we'll probably do a few from the REPL and such).
(def bucket
  "A sliding window of request timestamps and a token bucket."
  [(ref ()) (java.util.concurrent.Semaphore. 100 true)])

(def bgtask
  "Update bucket state once per minute."
  (future (while true (Thread/sleep 60000) (update!))))

(defn- update!
  "Move the sliding window along and add some tokens to the bucket."
  []
  (let [[window semaphore] bucket
        n                  (update-window! window)]
    (.release semaphore n)))

(defn- update-window!
  "Prune all timestamps older than an hour from WINDOW.
   Return the number of timestamps removed."
  [window]
  (dosync
   (let [now (timestamp)
         len (count @window)]
     (alter window (partial remove (fn [ts] (> (- now ts) 60))))
     (- len (count @window)))))

(defn- token
  "Get a token, or block until one is available."
  []
  (let [[window semaphore] bucket]
    (.acquire semaphore)
    (dosync (alter window (partial cons (timestamp))))))

(defn- timestamp [] (/ (/ (System/currentTimeMillis) 1000) 60))

;;;_* Emacs ============================================================
;;; Local Variables:
;;; allout-layout: t
;;; End:
