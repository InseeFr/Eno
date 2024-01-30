# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [3.16.1] - 2024-01-30
### :bug: Bug Fixes
- [`f487c48`](https://github.com/InseeFr/Eno/commit/f487c4863e439417f536b54f0302c183fbeb7ce4) - remove prefix in questionnaire id *(PR [#890](https://github.com/InseeFr/Eno/pull/890) by [@nsenave](https://github.com/nsenave))*
  - :arrow_lower_right: *fixes issue [#889](undefined) opened by [@romaintailhurat](https://github.com/romaintailhurat)*

### :construction_worker: Build System
- [`063e513`](https://github.com/InseeFr/Eno/commit/063e513a721b6b2d341a8d5a617945199fbcff63) - **release**: add github token to authorize auto commit *(commit by [@nsenave](https://github.com/nsenave))*

## [3.16.0] - 2024-01-29
### :construction_worker: Build System
- [`3a3b00c`](https://github.com/InseeFr/Eno/commit/3a3b00c6df38c3dc6c7e9cbd476770ce23792f03) - java 21 *(PR #879 by @nsenave)*

### :rocket: CI Workflow Changes
- [`0e339f5`](https://github.com/InseeFr/Eno/commit/0e339f5e5915378c947c3d0e133f3b50e59f19ef) - update release note action *(commit by @nsenave)*
- [`0c0a29e`](https://github.com/InseeFr/Eno/commit/0c0a29e1eb6d135f4317e467e78bc190f8d4da5b) - add dependabot config file *(commit by @nsenave)*
- [`ab1dd36`](https://github.com/InseeFr/Eno/commit/ab1dd36b58d1658fb7e918e8f797f84062e881e9) - write changelog file in release workflow *(commit by @nsenave)*
- [`a35ebf9`](https://github.com/InseeFr/Eno/commit/a35ebf9b1454e5b11d6cc2106c0aae287953eda4) - refine previous tag search regex *(commit by @nsenave)*
- [`6a7ffa9`](https://github.com/InseeFr/Eno/commit/6a7ffa95ae76f29a6d46df2bc2067cb1e9066060) - add 'latest' tag in docker publish *(commit by @nsenave)*

### :memo: Documentation Changes
- [`b3fe5d8`](https://github.com/InseeFr/Eno/commit/b3fe5d8e4d549b5a351359b131dd2a0392646c59) - init changelog file *(commit by @nsenave)*
- [`5e83285`](https://github.com/InseeFr/Eno/commit/5e8328565cfecce9d4f1a6d18e2b734a55365ee9) - add property to link release note in swagger *(commit by @nsenave)*

## [3.15.10] - 2024-01-27
### :recycle: Refactors
- [`b33ae13`](https://github.com/InseeFr/Eno/commit/b33ae13618510159cc82715d96525fa6c77d9176) - remove usage of deprecated Lunatic-Model method *(commit by @nsenave)*

### :memo: Documentation Changes
- [`8011be4`](https://github.com/InseeFr/Eno/commit/8011be4786cc9a604acd464d8c38e2a29f366c6e) - add javadoc on Eno parameters
- [`2eefa4b`](https://github.com/InseeFr/Eno/commit/2eefa4bc0c7a9c7941d395de5e634525be3d5cb0) - add javadoc in Eno questionnaire
- [`04f383f`](https://github.com/InseeFr/Eno/commit/04f383fafec8bd2cc6428b87c22231137a791ec2) - add javadoc in label objects
- [`84abca3`](https://github.com/InseeFr/Eno/commit/84abca3f064feb994475db46a88958983ea7bff1) - update javadoc in code list classes
- [`cf566bc`](https://github.com/InseeFr/Eno/commit/cf566bc957c2605c74b065096e30a47e19b8eccb) - add javadoc in binding reference class

### :construction_worker: Build System
- [`c133df3`](https://github.com/InseeFr/Eno/commit/c133df36edbcd2f05bcf0a6299dd102bb807c1c7) - bump org.springframework.boot from 3.2.0 to 3.2.2 (#871)
- [`39b1687`](https://github.com/InseeFr/Eno/commit/39b1687791bd68f62d7afe443ab933f24953e205) - bump com.networknt:json-schema-validator from 1.1.0 to 1.2.0 (#870)

## [3.15.9] - 2024-01-22
### :bug: Bug Fixes
- [`aad6fb4`](https://github.com/InseeFr/Eno/commit/aad6fb4e9cb898f40418d0c09b89250f84a689ff) - ddi expression references resolution *(PR #873 by @nsenave)*

## [3.15.8] - 2024-01-19
### :bug: Bug Fixes
- [`7f24b39`](https://github.com/InseeFr/Eno/commit/7f24b390bb26b7902ad7d4555ec8e5b4497c5a96) - lunatic shape from *(PR #869 by @nsenave)*
  - :arrow_lower_right: *fixes issue #864 opened by @AnneHuSKa*

## [3.15.7] - 2024-01-17
### :recycle: Refactors
- [`ad5078b`](https://github.com/InseeFr/Eno/commit/ad5078b64e1b9cb80212869778d6ac63bf6ce008) - null cases handling for ddi code lists *(PR #863 by @nsenave)*
  - :arrow_lower_right: *fixes issue #862 opened by @AnneHuSKa*

## [3.15.6] - 2024-01-17
### :bug: Bug Fixes
- [`53a7dce`](https://github.com/InseeFr/Eno/commit/53a7dce380fca6ce023dc78623cd26656e324b5b) - **controls**: info criticality everywhere *(PR #861 by @nsenave)*

## 3.15.5 - 2024-01-15

:tada: First production release of Eno _Java_.

:sparkles: Main feature: DDI to Lunatic transformation.

:recycle: Eno web API has been enhanced.

[3.15.6]: https://github.com/InseeFr/Eno/compare/3.15.5...3.15.6
[3.15.7]: https://github.com/InseeFr/Eno/compare/3.15.6...3.15.7
[3.15.8]: https://github.com/InseeFr/Eno/compare/3.15.7...3.15.8
[3.15.9]: https://github.com/InseeFr/Eno/compare/3.15.8...3.15.9
[3.15.10]: https://github.com/InseeFr/Eno/compare/3.15.9...3.15.10
[3.16.0]: https://github.com/InseeFr/Eno/compare/3.15.10...3.16.0
[3.16.1]: https://github.com/InseeFr/Eno/compare/3.16.0...3.16.1
