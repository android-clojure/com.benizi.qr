(ns com.benizi.qr.main
    (:require [neko.activity :refer [defactivity set-content-view!]]
              [neko.debug :refer [*a]]
              [neko.data :as d]
              [neko.intent :as i]
              [neko.notify :refer [toast]]
              [neko.resource :as res]
              [neko.find-view :refer [find-view]]
              [neko.threading :refer [on-ui]])
    (:import [android.widget EditText]
             [android.content BroadcastReceiver Intent]
             [com.google.zxing.integration.android IntentResult
              IntentIntegrator]
             [java.net DatagramPacket DatagramSocket InetAddress]))

;; We execute this function to import all subclasses of R class. This gives us
;; access to all application resources.
(res/import-all)

(defn get-user-input
  [activity]
  (-> activity
      (find-view ::user-input)
      .getText))

(defn notify-from-edit
  "Finds an EditText element with ID ::user-input in the given activity. Gets
  its contents and displays them in a toast if they aren't empty. We use
  resources declared in res/values/strings.xml."
  [activity]
  (let [^EditText input (.getText (find-view activity ::user-input))]
    (toast (if (empty? input)
             (res/get-string R$string/input_is_empty)
             (res/get-string R$string/your_input_fmt input))
           :long)))

(defn send-packet
  [host port msg-string]
  (let [addr (InetAddress/getByName host)
        socket (DatagramSocket.)
        msg (.getBytes msg-string)
        packet (DatagramPacket. msg (count msg) addr port)]
    (.start (Thread. (fn [] (.send socket packet))))))

(defn send-it
  [msg]
  (send-packet "10.30.30.42" 10000 msg))

(defactivity com.benizi.qr.WaitForIt
  :key :waiter
  :extends BroadcastReceiver
  (onReceive [this ctx intent]
             (-> (*a :main)
                 IntentIntegrator.
                 .initiateScan)))

;; This is how an Activity is defined. We create one and specify its onCreate
;; method. Inside we create a user interface that consists of an edit and a
;; button. We also give set callback to the button.
(defactivity com.benizi.qr.MainActivity
  :key :main
  (onActivityResult
   [this req-code res-code ^Intent intent]
   (let [^EditText input (find-view (*a) ::user-input)
         ^IntentResult qr-code (IntentIntegrator/parseActivityResult req-code res-code intent)]
     (let [res-string (if qr-code
                        (.getContents qr-code)
                        "ERROR")]
       (send-it (str res-string "\n"))
       (.sendBroadcast (*a)
                       (i/intent (str (ns-name *ns*) ".GOT")
                                 {:qr res-string}))
       (.setText input res-string))))

  (onCreate [this bundle]
    (.superOnCreate this bundle)
    (neko.debug/keep-screen-on this)
    (on-ui
      (set-content-view! (*a)
        [:linear-layout {:orientation :vertical
                         :layout-width :fill
                         :layout-height :wrap}
         [:edit-text {:id ::user-input
                      :hint "Type text here"
                      :layout-width :fill}]
         [:button {:text "Get QR"
                   :on-click (fn [_]
                               (.initiateScan (IntentIntegrator. (*a))))}]
         [:button {:text "From Field"
                   :on-click (fn [_]
                               (-> (get-user-input) (str "\n") send-it))}]
         [:button {:text "PACKET"
                   :on-click (fn [_] (send-it "test\n"))}]
         [:button {:text R$string/touch_me ;; We use resource here, but could
                                           ;; have used a plain string too.
                   :on-click (fn [_] (notify-from-edit (*a)))}]]))))
