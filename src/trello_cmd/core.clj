(ns trello-cmd.core
  (:gen-class)
  (:require [trello-cmd.trello-api :as trello]))

(defn command-not-found [& _] [{:display-text "could not understand command"}])

(defn mk-string [& strings] (clojure.string/join "\n" strings))

(defn get-help-text []
  (mk-string "available commands:"
             " - boards"
             " - lists <board-name>"
             " - cards <board-name> <list-name>"
             ""))

(def commands
  {"boards" (fn [] (trello/boards))
   "lists" (fn [board-name] (trello/lists-by-board-name board-name))
   "cards" (fn [board-name list-name]
             (trello/cards-by-board-name-and-list-name board-name list-name))})

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (if (= 0 (count args))
    (println (get-help-text))
    (let [cmd (get commands (first args) command-not-found)
          results (apply cmd (rest args))]
      (if (empty? results)
        (println "no results..."))
      (doseq [result results]
        (println (:display-text result))))))

;(-main)
;(-main "boards")
;(-main "lists" "todo")
;(-main "cards" "todo" "easy")

;(-main "boardsx")
;(-main "lists" "todox")
;(-main "cards" "todo" "easye")
