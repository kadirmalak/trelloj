(ns trello-cmd.core
  (:gen-class)
  (:require [trello-cmd.trello-api :as trello]))

(defn mk-string [& strings] (clojure.string/join "\n" strings))

(defn get-help-text []
  (mk-string "usage:"
             " - trelloj (lists boards)"
             " - trelloj <board-name> (lists board lists)"
             " - trelloj <board-name> <list-name> (lists list cards)"
             ""))

(defn command-not-found [& _] [{:display-text "could not understand command"}])

(def commands-by-arity
  {0 (fn [] (trello/boards))
   1 (fn [board-name] (trello/lists-by-board-name board-name))
   2 (fn [board-name list-name]
             (trello/cards-by-board-name-and-list-name board-name list-name))})

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (if (and (= 1 (count args)) (= "help" (first args)))
    (println (get-help-text))
    (let [cmd (get commands-by-arity (count args))
          results (if cmd
                    (apply cmd args)
                    (command-not-found))]
      (if (empty? results)
        (println "no results..."))
      (doseq [result results]
        (println (:display-text result))))))

;(-main)
;(-main "boards")
;(-main "lists" "todo")
;(-main "cards" "todo" "easy")
;(-main "cards" "todo" "easy" "asd")

;(-main "boardsx")
;(-main "lists" "todox")
;(-main "cards" "todo" "easye")
