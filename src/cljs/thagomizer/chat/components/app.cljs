(ns thagomizer.chat.components.app
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [thagomizer.chat.components.accents :refer [online-users]]
   [thagomizer.common.components.accents :refer [header]]
   [thagomizer.common.components.input :refer [button]]
   [thagomizer.chat.components.messages :refer [messages]]
   [thagomizer.chat.components.typing :refer [typing-indicator]]
   [thagomizer.chat.events.sms :as sms-events]
   [thagomizer.chat.components.input :refer [input-text-field]]
   [thagomizer.chat.components.camera :refer [camera-modal]]
   [thagomizer.chat.events.camera :as camera-events]
   [thagomizer.common.components.utils :as c-utils]))

(defn get-client-rect [node]
  (let [r (.getBoundingClientRect node)]
    {:left (.-left r)
     :top (.-top r)
     :right (.-right r)
     :bottom (.-bottom r)
     :width (.-width r)
     :height (.-height r)}))

(defn handler [state this]
  (reset! state (get-client-rect (rdom/dom-node this))))
          
(defn chat-app []
  (let [state (r/atom {})]
  
  (r/create-class
   {:component-did-mount
    (fn [this]
      (.addEventListener js/window "resize" #(handler state this))
      (set! (.-onresize js/window)
                             (r/force-update this))
      (handler state this))
    
    :component-will-unmount
    (fn [this]
      (.removeEventListener js/window "resize" #(handler state this)))
    
    :reagent-render
    (fn []
    [:div#flex-container {:ref #(when % (partial handler state %))
     :style (merge {:flex-flow "column"
                    :display "flex"
                    :align-items "stretch"
                    :height "90vh"
                    :width "100%"} c-utils/center-css)}
     [header]
     [messages]
     [camera-modal state]
     [:div {:key "typing-indicator"}
      [typing-indicator]]
     [:div {:key "input-text-field"}
      [input-text-field]]
     [:div {:key "online-users"}
      [online-users]]
     [:div {:style {:margin "0 auto"}
            :key "buttons"}
      [button [::camera-events/camera-stream-and-modal] "img" "snap?"]
      [button [::sms-events/send-sms] "sms" "moo?"]]]
            )})))