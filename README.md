[![CircleCI](https://img.shields.io/circleci/project/github/johannesloetzsch/fn2api.svg?label=tests)](https://circleci.com/gh/johannesloetzsch/fn2api)
[![Clojars](https://img.shields.io/clojars/v/fn2api.svg?colorB=blue)](https://clojars.org/fn2api)


## Architecture

Fn2Api constists of several modules for the individual features:

<!-- [![CljDoc](https://cljdoc.org/badge/fn2api)](https://cljdoc.org/jump/release/fn2api) -->

* **fn2api-core** [![CljDoc](https://cljdoc.org/badge/fn2api-core)](https://cljdoc.org/jump/release/fn2api-core)
The core functionality — Providing functions for parsing function signatures and specs

* **fn2api-lib** [![CljDoc](https://cljdoc.org/badge/fn2api-lib)](https://cljdoc.org/jump/release/fn2api-lib)
Contains interfaces for external libraries used in the other modules. This includes mounts for config parsing and logging.

* **fn2api-format** [![CljDoc](https://cljdoc.org/badge/fn2api-format)](https://cljdoc.org/jump/release/fn2api-format)
Encoding (serialization) and Decoding (parsing + coercion according to specs) for different file formats (including `edn`, `json`, `yaml`). 

### Targets

* **fn2api-cli** [![CljDoc](https://cljdoc.org/badge/fn2api-cli)](https://cljdoc.org/jump/release/fn2api-cli)
Generate commandline interfaces

* **fn2api-web** [![CljDoc](https://cljdoc.org/badge/fn2api-web)](https://cljdoc.org/jump/release/fn2api-web)
Generate web interfaces

## Tutorial and further documentation

… should follow soon.

For now please have a look at the `./examples` and the testcases for the individual `./modules`.
