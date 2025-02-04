# Change Log

The format is based on [Keep a Changelog](http://keepachangelog.com/).

## [4.0] - 2025-01-29
### Changed
- `pom.xml`: moved to `io.kstuff` (package amd Maven group)

## [3.1] - 2025-01-26
### Changed
- `pom.xml`: updated Kotlin version to 2.0.21, updated dependency version
- tests: converted to `should-test` library

## [3.0] - 2024-07-22
### Added
- `build.yml`, `deploy.yml`: converted project to GitHub Actions
### Changed
- `pom.xml`: updated Kotlin version to 1.9.24, updated dependency version
- `URICoDecoder`: renamed from `URICODecoder` (breaking change)
### Removed
- `.travis.yml`

## [2.4] - 2023-12-02
### Changed
- `pom.xml`: updated dependency versions

## [2.3] - 2023-11-10
### Changed
- `pom.xml`: updated dependency versions

## [2.2] - 2023-07-24
### Changed
- `pom.xml`: updated Kotlin version to 1.8.22
- `pom.xml`: updated dependency versions

## [2.1] - 2023-06-04
### Changed
- `CoDecoders.kt`: changed `getDecoder()` and `getEncoder()` to use case-insensitive comparison on charset name

## [2.0] - 2023-05-19
### Changed
- `codec` classes: added error handling using `ErrorStrategy`
- `codec` classes: renamed classes to reflect the fact that encoding and decoding primarily uses UTF-16
  (breaking change)

## [1.2] - 2023-05-07
### Changed
- `XMLCoDecoder`: fixed issue from conversion
- `pom.xml`: updated dependency version

## [1.1] - 2023-05-04
### Added
- `CoDecoderBase`, `CoEncoderBase`, `HTMLCoDecoder`, `HTMLCoEncoder`, `XMLCoDecoder`, `XMLCoEncoder`

## [1.0.1] - 2023-04-25
### Changed
- `pom.xml`: updated version because of Sonatype upload failure

## [1.0] - 2023-04-25
### Added
- `CoFunctions`: extension functions for `CoAcceptor` and `IntCoAcceptor`
### Changed
- `CoUtility`: renamed from original `CoFunctions`
- `pom.xml`: promoted to version 1.0

## [0.9] - 2023-04-23
### Changed
- `pom.xml`: updated dependency version

## [0.8] - 2023-04-16
### Changed
- Major revision

## [0.6] - 2020-09-17
### Changed
- `pom.xml`: updated Kotlin version to 1.4.0

## [0.5] - 2020-05-01
### Changed
- `BaseCoAcceptor`: added `flush()`
- `AbstractCoPipeline`, `AbstractIntObjectCoPipeline`, `AbstractIntCoPipeline`: propagate `flush()`

## [0.4.1] - 2020-04-21
### Changed
- `pom.xml`: updated dependency versions

## [0.4] - 2020-04-19
### Added
- `ByteChannelCoAcceptor`: sends data to a Kotlin `ByteWriteChannel`
- `CoStrings.kt`: various functions to handle character output

## [0.3] - 2020-02-18
### Added
- `ChannelCoAcceptor`: sends data to a Kotlin `Channel`

## [0.2] - 2020-02-14
### Changed
- Split main file into three, added KDoc
### Added
- More unit tests

## [0.1] - 2020-02-08
### Added
- Initial versions - work in progress
