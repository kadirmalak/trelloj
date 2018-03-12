(ns trello-cmd.trello-api
  (:require [clojure.data.json :as json])
  (:require [clj-http.client :as client])
  (:import (java.io File)))

(def config (json/read-str (slurp (str
                                    (System/getProperty "user.home")
                                    (File/separator)
                                    ".trello.json"))))

(def BASE (get config "base"))
(def KEY (get config "key"))
(def TOKEN (get config "token"))

(defn fetch [url f]
  (let [http-response (client/get url {:cookie-policy :none})
        response (json/read-str (:body http-response))]
    (map f response)))

(defn boards []
  (let [url (format "%s/members/me/boards?key=%s&token=%s&filter=open" BASE KEY TOKEN)]
    (fetch url (fn [el] {:name (get el "name")
                         :id (get el "id")
                         :display-text (get el "name")}))))

(defn lists [board-id]
  (let [url (format "%s/boards/%s/lists/open?key=%s&token=%s" BASE board-id KEY TOKEN)]
    (fetch url (fn [el] {:name (get el "name")
                         :id (get el "id")
                         :display-text (get el "name")}))))

(defn lists-by-board-name [board-name]
  (let [boards (boards)
        board (first (filter #(= board-name (:name %)) boards))]
    (if board
      (lists (:id board))
      [])))

(defn cards [list-id]
  (let [url (format "%s/lists/%s/cards?key=%s&token=%s" BASE list-id KEY TOKEN)]
    (fetch url (fn [el] {:name (get el "name")
                         :id (get el "id")
                         :display-text (get el "name")}))))

(defn cards-by-board-name-and-list-name [board-name list-name]
  (let [lists (lists-by-board-name board-name)
        list (first (filter #(= list-name (:name %)) lists))]
    (if list
      (cards (:id list))
      [])))

;(boards)
;(lists-by-board-name "Machine Learning")
;(cards-by-board-name-and-list-name "Machine Learning" "ToDo")
;(lists "59d52305d790a89a29d66352")
;(cards "5a26b45581d748f686a69933")
