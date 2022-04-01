(ns spwz.core
  (:require [zen.core]
            [zen.dev]))


(defonce ztx (zen.core/new-context {:paths [(str (System/getProperty "user.dir") "/zrc")]}))
#_(reset! ztx @(zen.core/new-context {:paths [(str (System/getProperty "user.dir") "/zrc")]}))


(zen.core/read-ns ztx 'my-zen-ns)


(zen.dev/watch ztx)
#_(zen.dev/stop ztx)


(zen.core/get-symbol ztx 'my-zen-ns/Patient)


(zen.core/errors ztx)


(zen.core/validate ztx
                   #{'my-zen-ns/Patient}
                   {:id "pt1"
                    :resourceType "Patient"
                    :name [{:given ["Yurii"]
                            :family "Doe"}]
                    :active true})


(defn validate-resource [ztx resource]
  (let [rt                           (:resourceType resource)
        persistent-schema-symbols    (zen.core/get-tag ztx 'my-zen-ns/persistent-resource)
        this-resource-schema-symbols (filter (fn [sym] (= rt (:resourceType (zen.core/get-symbol ztx sym))))
                                             persistent-schema-symbols)]
    (zen.core/validate ztx
                       this-resource-schema-symbols
                       resource)))


(validate-resource
  ztx
  {:id "pt1"
   :resourceType "Patient"
   :name [{:given  ["Yurii"]
           :family "Doe"}]
   :active true})


(zen.core/get-tag ztx 'my-zen-ns/persistent-resource)
