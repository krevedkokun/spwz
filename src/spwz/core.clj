(ns spwz.core
  (:require [zen.core]
            [zen.dev]))


(defonce ztx (zen.core/new-context {:paths [(str (System/getProperty "user.dir") "/zrc")]}))
#_(reset! ztx @(zen.core/new-context {:paths [(str (System/getProperty "user.dir") "/zrc")]}))


(zen.core/read-ns ztx 'my-zen-ns)


(zen.dev/watch ztx)
#_(zen.dev/stop ztx)


(zen.core/get-symbol ztx 'my-stuff.my-schemas/abstract-resource)


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
        profile-uris                 (set (get-in resource [:meta :profile]))

        profile-schema-symbols               (zen.core/get-tag ztx 'my-stuff.my-schemas/profile-schema)
        this-resource-profile-schema-symbols (filter (fn [sym] (contains?
                                                                 profile-uris
                                                                 (:profileUri (zen.core/get-symbol ztx sym))))
                                                     profile-schema-symbols)


        persistent-schema-symbols    (zen.core/get-tag ztx 'my-zen-ns/persistent-resource)
        this-resource-schema-symbols (filter (fn [sym] (= rt (:resourceType (zen.core/get-symbol ztx sym))))
                                             persistent-schema-symbols)]
    (zen.core/validate ztx
                       (concat
                         this-resource-profile-schema-symbols
                         this-resource-schema-symbols)
                       resource)))


(validate-resource
  ztx
  {:id "pt1"
   :resourceType "Patient"
   :name [{:given  ["Yurii"]
           :family "Doe"}]
   :active true})


(zen.core/get-tag ztx 'my-stuff.my-schemas/persistent-resource)


(zen.core/get-tag ztx 'my-stuff.my-schemas/profile-schema)


(validate-resource
  ztx
  {:id "pr1"
   :resourceType "Practitioner"
   :meta {:profile ["zen-demo-profiles:practitioner"]}
   :name [{:given  ["John"]
           :family "Doe"}]
   :telecom [{:system "phone"
              :use    "work"
              :value  "8800555"}
             {:system "email"
              :use    "work"
              :value "john.doe@example.com"}
             {:system "phone!"
              :use    "mobile"
              :value  "88005553535"}]})
