(ns vcard-converter.core
  (:require [clojure.string :as cs]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn encode-str [s]
  (-> s
      (cs/replace #"([,;])" "\\$1")
      (cs/replace #"\n" "\\n")))

(defn format-vcard [contact]
  (let [parts (cond-> ["BEGIN:VCARD"
                       "VERSION:3.0"
                       (format "FN;CHARSET=UTF-8:%s" (encode-str (:name contact)))]

                      (or (:firstName contact) (:lastName contact))
                      (conj (format "N;CHARSET=UTF-8:%s;%s;;" (:lastName contact) (:firstName contact)))

                      (:photo contact)
                      (conj (format "PHOTO;VALUE=URI;TYPE=" (str (get-in contact [:photo :type]) ":%s" (encode-str (:uri contact)))))

                      (:email contact)
                      (conj (format "EMAIL;CHARSET=UTF-8:%s" (encode-str (:email contact))))

                      (get-in contact [:phones :mobile])
                      (conj (format "TEL;TYPE=CELL:%s" (encode-str (get-in contact [:phones :mobile]))))

                      (get-in contact [:phones :home])
                      (conj (format "TEL;TYPE=HOME,VOICE:%s" (encode-str (get-in contact [:phones :home]))))

                      (get-in contact [:phones :office])
                      (conj (format "TEL;TYPE=WORK,VOICE:%s" (encode-str (get-in contact [:phones :office]))))

                      (:title contact)
                      (conj (format "TITLE;CHARSET=UTF-8:%s" (encode-str (:title contact)))))
        parts' (concat parts [(format "REV:%s" (.toISOString))
                              "END:VCARD"])]
    (cs/join "\r\n" parts')))

(defn -main [contact]
  (println (format-vcard contact)))
