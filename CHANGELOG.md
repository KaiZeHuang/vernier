# Changelog

Notable changes to Vernier, newest first. Tag names are `v<date>-<channel>` and
version names are derived from git metadata; see `docs/architecture/README.md`.
The `nightly` release is a rolling build and is not listed here entry by entry.

Vernier is in alpha. A listed detector describes **designed coverage**, not a
measured result on any particular device. See each detector's `EVIDENCE.md` for
its visibility limits.

## [Unreleased]

### Added

- PlayIntegrityFix residue detection for `persist.sys.pihooks.first_api_level`.
- Raw boot parameter cross-checks for `ro.boot.warranty_bit` (Samsung Knox) and
  `ro.boot.realmebootstate`. `resetprop` edits the property area itself, so every
  read path agrees and source-consistency cannot see it; the kernel-supplied
  `androidboot.*` value in `/proc/cmdline` and `/proc/bootconfig` is out of reach.
- A build profile consistency check: `Build.TYPE` and `Build.TAGS` against the
  type and tags encoded in the tail of `Build.FINGERPRINT`. PlayIntegrityFix
  rewrites the first two but not the fingerprint.
- A detection coverage table in both READMEs: eighteen detectors, what each
  observes, and a direct link to its evidence record.
- `.editorconfig` so text files stay LF and indentation stays consistent across
  platforms.

### Changed

- Backup and device-transfer rules now exclude every writable domain instead of
  shipping as unconfigured Android Studio templates. `allowBackup="false"` was
  already the primary control; the rules are the second layer.

### Fixed

- Documentation claimed seventeen detectors. The code has eighteen. The profile
  README and both community post drafts were corrected.

## [v2026.10.24-alpha] - 2026-10-24

First public alpha.

- Eighteen detectors across five measurement groups, each with an `EVIDENCE.md`
  documenting the observable signal, its mechanism, primary references,
  applicability, visibility limits, result states and interpretation.
- The same detectors published as a UI-free SDK AAR, so another app can embed
  them without the Compose interface.
- Kotlin / Jetpack Compose UI with a C++ and assembly JNI layer for probes that
  cannot be answered from managed code.
- Multi-module Gradle build with module boundaries, native boundaries, JNI
  contracts, reflection boundaries, text protocols, evidence records and source
  file length enforced by seven CI guard scripts.
- Bilingual English / Simplified Chinese UI and documentation.
- CI that builds and publishes APK artifacts and nightly releases even with no
  secrets configured; release signing and Telegram notification are skipped when
  their secrets are absent.
- Tagged pre-releases (`-alpha`, `-beta`, `-rc`, `-preview`) published as GitHub
  pre-releases so they do not take over `latest`.
- The release rebuild baseline derived from the root commit rather than pinned
  to a SHA that no longer exists after history was rewritten.

[v2026.10.24-alpha]: https://github.com/KaiZeHuang/vernier/releases/tag/v2026.10.24-alpha
