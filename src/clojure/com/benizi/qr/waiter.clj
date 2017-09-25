(ns com.benizi.qr.waiter
  (:require [neko.debug :refer [*a]])
  (:import [com.google.zxing.integration.android IntentIntegrator])
  (:gen-class
   :name com.benizi.qr.waiter.WaitForIt
   :init init
   :extends android.content.BroadcastReceiver
   :constructors {[] []}
   :main false))

(defn -init
  []
  [[] (ref {})])

(defn -onReceive
  [this ctx intent]
  (-> (*a :main)
      IntentIntegrator.
      .initiateScan))
