# fields

[![Build
Status](https://travis-ci.com/stefanhengl/pdfsplit.svg?branch=master)](https://travis-ci.com/stefanhengl/pdfsplit)

`fields` exposes the function `select-keys-by-fields` which offers the
same functionality as
[select-keys](https://clojuredocs.org/clojure.core/select-keys) but
for complex nested maps. Instead of specifying paths in a vector, we write a query
describing the structure of the desired output map.

`fields`is inspired by the same-named HTTP request parameter as
described by Google
[here](https://developers.google.com/drive/api/v3/performance). In the
background it parses the query into a
[zipper](https://clojuredocs.org/clojure.zip) and traverses the zipper
filtering the map along the way.

## Example

```clojure
(def data {:a 1
	:b [{:aa 2 :bb 3} {:aa 4 :bb 5}]
	:c {:cc 6 :dd {:fff 7 :ggg 8}}})

(select-keys-by-fields data "(a)")
=> {:a 1}

(select-keys-by-fields data "(a,b(aa))")
=> {:a 1, :b ({:aa 2} {:aa 4})}

(select-keys-by-fields data "(b,c(cc,dd(ggg)))")
=> {:b ({:aa 2, ::bb 3} {:aa 4, :bb 5}), :c {:cc 6, :dd {:ggg 8}}}
```

## License
 MIT
