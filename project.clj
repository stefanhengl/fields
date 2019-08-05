(defproject fields "0.1.0-SNAPSHOT"
  :description "A library to select keys from nested maps. Inspired by the fields request parameter propsed by Google."
  :scm {:name "git" :url "https://github.com/stefanhengl/fields" }
  :url "https://github.com/stefanhengl/fields"
  :license {:name "MIT"
            :url "https://github.com/stefanhengl/fields/LICENSE"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :deploy-repositories [["releases" {:url "https://clojars.org/repo"
                                     :creds :gpg}]]
  :repl-options {:init-ns fields.core})
