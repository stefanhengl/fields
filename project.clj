(defproject fields "0.1.0"
  :description "Like select-keys but for nested maps. Inspired by the fields request parameter proposed by Google."
  :scm {:name "git" :url "https://github.com/stefanhengl/fields" }
  :url "https://github.com/stefanhengl/fields"
  :license {:name "MIT"
            :url "https://github.com/stefanhengl/fields/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :deploy-repositories [["releases" {:url "https://clojars.org/repo"}]]
  :repl-options {:init-ns fields.core})
