# Change Log

The format is based on [Keep a Changelog](http://keepachangelog.com/).

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
