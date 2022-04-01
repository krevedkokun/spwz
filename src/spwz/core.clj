(ns spwz.core
  (:require [zen.core]
            [zen.dev]))


(defonce ztx (zen.core/new-context {:paths [(str (System/getProperty "user.dir") "/zrc")]}))
#_(reset! ztx @(zen.core/new-context {:paths [(str (System/getProperty "user.dir") "/zrc")]}))


(zen.core/read-ns ztx 'my-zen-ns)


(zen.dev/watch ztx)
#_(zen.dev/stop ztx)


(zen.core/errors ztx)


(defn validate-resource [ztx resource]
  (let [rt                           (:resourceType resource)
        profile-uris                 (set (get-in resource [:meta :profile]))

        profile-schema-symbols               (zen.core/get-tag ztx 'zen.fhir/profile-schema)
        this-resource-profile-schema-symbols (filter (fn [sym] (contains?
                                                                 profile-uris
                                                                 (:zen.fhir/profileUri (zen.core/get-symbol ztx sym))))
                                                     profile-schema-symbols)


        persistent-schema-symbols    (zen.core/get-tag ztx 'zen.fhir/base-schema)
        this-resource-schema-symbols (filter (fn [sym] (= rt (:zen.fhir/type (zen.core/get-symbol ztx sym))))
                                             persistent-schema-symbols)]
    (zen.core/validate ztx
                       (concat
                         this-resource-profile-schema-symbols
                         this-resource-schema-symbols)
                       resource)))


(zen.core/get-tag ztx 'my-stuff.my-schemas/persistent-resource)


(zen.core/get-tag ztx 'my-stuff.my-schemas/profile-schema)


(zen.core/get-symbol ztx 'my-zen-ns/PractitionerProfile)


(validate-resource
  ztx
  {:id           "pr1"
   :resourceType "Practitioner"
   :meta         {:profile ["zen-demo-profiles:practitioner"]}
   :name         [{:given  ["John"], :family "Doe"}]
   :telecom      [{:system "phone"
                   :use    "work"
                   :value  "8800555"}
                  {:system "email"
                   :use    "work"
                   :value  "john.doe@example.com"}
                  {:system "pager"
                   :use    "home"
                   :value  "22222"}]})
