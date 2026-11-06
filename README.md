<p align="center">
  <img src="app/src/main/ic_launcher-playstore.png" alt="Vernier logo" width="128">
</p>

<h1 align="center">Vernier</h1>

<p align="center"><strong>On-device Android environment integrity diagnostics backed by Kotlin and native probes.</strong></p>

<p align="center">
  <strong>
    <a href="#quick-start">Documentation</a> ·
    <a href="docs/guides/sdk-integration.md">SDK Integration</a>
  </strong>
</p>

<p align="center">
  <a href="https://github.com/KaiZeHuang/vernier/actions/workflows/build.yml"><img src="https://img.shields.io/github/actions/workflow/status/KaiZeHuang/vernier/build.yml?branch=main&amp;style=flat-square&amp;label=build&amp;logo=githubactions&amp;logoColor=white" alt="Main branch build workflow status"></a>
  <a href="LICENSE"><img src="https://img.shields.io/github/license/KaiZeHuang/vernier?style=flat-square&amp;label=license&amp;logo=apache&amp;logoColor=white" alt="Apache-2.0 license"></a>
  <a href="https://github.com/KaiZeHuang/vernier/releases"><img src="https://img.shields.io/github/v/release/KaiZeHuang/vernier?style=flat-square&amp;include_prereleases&amp;label=release" alt="Latest release"></a>
  <a href="https://github.com/KaiZeHuang/vernier/releases"><img src="https://img.shields.io/github/downloads/KaiZeHuang/vernier/total?style=flat-square&amp;label=downloads" alt="Total downloads"></a>
  <a href="#compatibility"><img src="https://img.shields.io/badge/Android-10%2B-3DDC84?style=flat-square&amp;logo=android&amp;logoColor=white" alt="Android 10 or later"></a>
</p>




<p align="center">
  <a href="README.md">English</a> &nbsp; <a href="README_ZH.md">简体中文</a>
</p>

# Overview

Vernier collects and correlates security-relevant evidence on an Android device. It looks for boot-state changes, root and SU artifacts, runtime injection or hooking, mount namespace anomalies, modified system properties, suspicious applications, KeyStore and attestation inconsistencies, and signs of virtualization.

The app combines a Jetpack Compose interface, feature-oriented Gradle modules, and a native C++/assembly library. The same detectors are also published as a UI-free SDK AAR that other applications can embed. Results are diagnostic signals, not an authoritative statement that a device is secure or compromised.

# Detection coverage

Eighteen detectors, grouped the way the dashboard groups them: how the device booted and what it attests, what runs in this process, what the filesystem shows, what the policy and property layers claim, and the environment around all of it.

Every detector ships an `EVIDENCE.md` stating what it observes, why that counts as evidence, and — just as importantly — what it cannot see, with kernel and AOSP source references. This table describes **designed coverage**, not measured results on any particular device; coverage varies by OEM, kernel and Android version, and a probe that is unsupported reports that explicitly instead of guessing.

| Detector | What it observes | Record |
| :--- | :--- | :--- |
| `bootloader` | attested boot state, boot consistency, VBMeta digest of empty input, Samsung warranty fuse, Widevine | [record](feature/bootloader/EVIDENCE.md) |
| `tee` | attestation chain and RootOfTrust, KeyStore behaviour, generateKey reply, Keystore2 binder and grant metadata, SOTER, timing side channel | [record](feature/tee/EVIDENCE.md) |
| `playintegrityfix` | PIF residue properties, source consistency, runtime maps | [record](feature/playintegrityfix/EVIDENCE.md) |
| `zygisk` | linker and namespace integrity, memory map families, threads, descriptors, FD trap | [record](feature/zygisk/EVIDENCE.md) |
| `lsposed` | runtime classes and loaders, stacks and binder bridge, Zygote permission GIDs, dirty policy rules, module metadata | [record](feature/lsposed/EVIDENCE.md) |
| `memory` | symbol resolution and entry prologues, mappings and loader visibility, signal handlers | [record](feature/memory/EVIDENCE.md) |
| `heapresidue` | ART String residue of Java startup arguments, isolated-process collection and GC timing | [record](feature/heapresidue/EVIDENCE.md) |
| `mount` | mount tables, cross-view consistency, Zygote Next and isolated-process mount views, startup preload | [record](feature/mount/EVIDENCE.md) |
| `su` | su binaries and root daemons, process context, suspicious processes | [record](feature/su/EVIDENCE.md) |
| `nativeroot` | KernelPatch supercalls, netlink boundary, KernelSU interfaces, SUSFS side channel, cgroup and mount namespace, fsnotify SRCU timing | [record](feature/nativeroot/EVIDENCE.md) |
| `rootmanagers` | launcher visibility, manager signing keys, native payloads, hidden icons, manifest anchors | [record](feature/rootmanagers/EVIDENCE.md) |
| `selinux` | enforcement status, dirty policy rules, context validity, policy reload, audit integrity, SID-table consistency, App Zygote AVC profile | [record](feature/selinux/EVIDENCE.md) |
| `systemproperties` | security property catalog, source consistency, build profile consistency, raw boot parameters, property area modification times | [record](feature/systemproperties/EVIDENCE.md) |
| `customrom` | ROM properties and build fields, bootloader lock state, ROM services, packages and native residue | [record](feature/customrom/EVIDENCE.md) |
| `adbruntime` | settings and lifecycle context, USB runtime broadcast, ADB property context, network ADB mDNS, wire protocol identity | [record](feature/adbruntime/EVIDENCE.md) |
| `dangerousapps` | PackageManager inventory, filesystem and provider methods, tool-specific channels | [record](feature/dangerousapps/EVIDENCE.md) |
| `kernelcheck` | kernel identity consistency, community kernel markers, boot cmdline and kernel pointers, ARM64 CPU identity | [record](feature/kernelcheck/EVIDENCE.md) |
| `virtualization` | properties and guest services, native bridge and translated execution, graphics and honeypots, host app consistency | [record](feature/virtualization/EVIDENCE.md) |

# Quick start

1. **Build** the APK yourself with `./gradlew :app:assembleDebug`, or grab a published build from the [releases](https://github.com/KaiZeHuang/vernier/releases).
2. **Install** it on a device running Android 10 or later. Root access is not required.
3. **Scan.** The first launch asks you to accept the user agreement and to settle a few startup choices: notifications, Live Update, the online revocation refresh used by the TEE check, and package visibility. Scanning then starts by itself, and each card fills in when its detector finishes.
4. **Read the results** as described below. To report a result or ask for help, [open an issue](https://github.com/KaiZeHuang/vernier/issues) and attach the file saved by **Export Report** at the top of the dashboard; a screenshot of the summary alone is not enough.

## Reading the results

Each card shows one of these statuses:

| Status | Meaning |
| :--- | :--- |
| **Danger**, **Warning** | The detector observed evidence; the card lists what it saw. |
| **All Clear** | The probes ran and found none of the evidence they look for. This does not prove that the device is unmodified. |
| **Info** (Support) | A probe was unsupported or unavailable on this device, so the absence of findings says nothing. |
| **Info** (Error) | The check failed; the card says why. |

To see what a detector looks for and why that counts as evidence, open its evidence record from [Detector coverage](#detector-coverage).

# Detector coverage

Current feature areas, each linked to its evidence record, which explains what the detector looks for, why that counts as evidence, and what it cannot see:

[`ADB Runtime`](./feature/adbruntime/EVIDENCE.md) · [`Bootloader`](./feature/bootloader/EVIDENCE.md) · [`Custom ROM`](./feature/customrom/EVIDENCE.md) · [`Dangerous Apps`](./feature/dangerousapps/EVIDENCE.md) · [`Kernel Check`](./feature/kernelcheck/EVIDENCE.md) · [`LSPosed`](./feature/lsposed/EVIDENCE.md) · [`Memory`](./feature/memory/EVIDENCE.md) · [`Mount`](./feature/mount/EVIDENCE.md) · [`Native Root`](./feature/nativeroot/EVIDENCE.md) · [`Play Integrity Fix`](./feature/playintegrityfix/EVIDENCE.md) · [`Root Managers`](./feature/rootmanagers/EVIDENCE.md) · [`SELinux`](./feature/selinux/EVIDENCE.md) · [`SU`](./feature/su/EVIDENCE.md) · [`System Properties`](./feature/systemproperties/EVIDENCE.md) · [`TEE`](./feature/tee/EVIDENCE.md) · [`Virtualization`](./feature/virtualization/EVIDENCE.md) · [`Zygisk`](./feature/zygisk/EVIDENCE.md)

The SELinux area also retains an app-zygote SID-table query/registration experiment. Repeated discrepancies are supporting warnings; synchronized policy hiding can leave no discrepancy. Controlled privileged-context writes run in a disposable child with malformed/stock controls; recognition and ordinary-app timing are supporting warnings, an app-zygote AVC lookup-count profile is informational only, and negative observations cannot exclude hidden policy. Access failures and incomplete experiments remain explicit coverage states. See the [SELinux evidence record](./feature/selinux/EVIDENCE.md) for source references and device-validation limits.

`Play Integrity Fix` refers to local indicators associated with integrity-spoofing modifications; Vernier does not return an official Google Play Integrity API verdict. The TEE area evaluates Android KeyStore and attestation evidence, including certificate chains, security levels, revocation data, and selected KeyMint, StrongBox, and Soter behavior where supported.

Native Root also offers an optional [fsnotify SRCU timing experiment](feature/nativeroot/SRCU_TIMING.md), disabled by default. It requires an explicit choice, uses a temporary private dynamic permission on supported Android 12–14 systems, and reports repeatable contention as supporting evidence. Its thresholds await device calibration; it does not identify KernelSU, and its private-process deadline cannot recover a blocked kernel.

Supporting modules provide the dashboard, device information, settings, update checks, notifications, license information, and common UI infrastructure.

# How it works

- **Feature modules:** each detector owns its collection, judgement rules, report projection, and UI in its own Gradle modules, while collection code that several detectors share lives in capability modules.
- **Early native capture:** a transparent `NativeActivity` runs before `MainActivity` and records early mount and virtualization evidence.
- **Native probes:** the shared native library performs checks that require direct system calls, `/proc` inspection, timing measurements, linker/runtime visibility, or architecture-specific assembly.
- **Process separation:** selected Zygisk, Mount, Native Root, virtualization, SELinux, LSPosed, and TEE checks run in dedicated or isolated services so evidence can be compared across process boundaries.
- **Evidence correlation:** the dashboard reports individual findings and coverage states instead of treating one heuristic as conclusive.

# Compatibility

| Area | Details |
| :--- | :--- |
| **Android** | Android 10 or later (`minSdk 29`); compiled and targeted against Android API 37. |
| **ABIs** | Native syscall paths are provided for `arm64-v8a`, `armeabi-v7a`, `x86`, and `x86_64`. Some timing and virtualization trap probes are available only on `arm64-v8a`. |
| **Privileges** | Root access is not required. Android permissions and platform visibility rules still limit what the app can observe. |
| **Device variance** | OEM changes, kernel configuration, Android version, and sandbox policy may cause a probe to be unsupported, unavailable, or lower-confidence. |
| **Network use** | Core device scans run locally. TEE revocation checks always include the bundled snapshot; downloading Google's current revocation feed requires user consent. The app also checks GitHub for Nightly updates after a cold start and when requested from Settings. With GitHub acceleration on, which Chinese-language users are asked about and Settings can switch, those checks and the Nightly download go through the third-party [gh-proxy.com](https://gh-proxy.com/docs) service instead. |

# Architecture

```text
vernier/
├─ app/                   # UI composition root: activities, dashboard cards, notifications
├─ sdk/runtime/           # Headless composition root: detector catalog, scan API, process hooks, native library registry
├─ sdk/aar/               # The distributable SDK: every headless module fused into one AAR
├─ core/                  # Contracts and shared infrastructure: evidence, native payloads, platform access, reports, scan lifecycle, detector, UI
├─ capability/            # Evidence collection shared by several detectors
├─ feature/               # One detector or supporting feature per directory, split into layers
├─ samples/sdk-consumer/  # A separate application built from the published AAR alone
├─ build-logic/           # Convention plugins, module boundary and dependency validation, generated assets
├─ uihelper/              # Pinned Git submodule: MIUIX / Material UI, included as a separate Gradle build
├─ docs/                  # Architecture overview, guides, and follow-ups
├─ .github/policies/      # Module, native, detector touch point, reflection, and text protocol policies
├─ .github/scripts/       # Repository guards and their self-tests
├─ gradle/                # Version catalog and Gradle wrapper files
├─ scripts/               # Detector scaffold and repository maintenance scripts
├─ build.gradle.kts
└─ settings.gradle.kts
```

Each detector is a set of Gradle modules under `feature/<name>/`:

- `domain`: result models, report models, status definitions, and judgement rules (pure JVM)
- `data`: probes, parsers, repositories, JNI bridges, and Android service access
- `presentation`: the card model, its mapping from the domain report, and the report projection (pure JVM)
- `detector`: the one headless `<Name>Detector` object that binds the other layers for the SDK and the app
- `ui`: the Compose card, the prompts for the detector's consents, and its integration with the dashboard

Dependencies point from `:app` to the SDK, from both to features, from features to capabilities, and from capabilities to core; features never depend on each other. Every module declares exactly the dependencies it uses. The build and CI enforce these rules. See the [architecture overview](./docs/architecture/README.md).

A new detector touches only its own directory and its one line in `DetectorCatalog`, plus one registry line and one policy entry when it has a native unit; the app generates its dashboard cards from the ui modules. One command creates all of it, in a state that builds and reports "Not evaluated" until its probe is written:

```bash
python3 scripts/new_detector.py <name> --description "what it looks for and why that is evidence"
```

Continue with [Adding a detector](./docs/guides/adding-a-detector.md). To run the detectors inside another application, see [Using the SDK](./docs/guides/sdk-integration.md).

Native sources are organized into units. Each unit lives in `src/main/cpp/<unit>/` of the module that owns it, is compiled as its own object library and is linked into `libvernier.so`; Kotlin code reaches native results through the JNI bridges of that same module.

# Build

## Requirements

- Android Studio with Android SDK Platform 37 and Build Tools 37.0.0
- JDK 17
- Android NDK 30.0.16138531
- CMake 4.1.2

Dependency and tool versions are defined in [`gradle.properties`](./gradle.properties) and [`gradle/libs.versions.toml`](./gradle/libs.versions.toml).

Initialize the UI submodule after cloning (or clone with `--recurse-submodules`). It is an
independent Gradle build; the detector SDK never depends on it:

```bash
git submodule update --init --recursive
```

## Commands

```bash
# macOS / Linux
./gradlew :app:assembleDebug

# Windows
gradlew.bat :app:assembleDebug
```

For the standard local validation path:

```bash
./gradlew unitTest :app:assembleDebug :app:lintDebug buildHealth
for s in .github/scripts/check-*.py; do python3 "$s"; done
```

The same build also produces the headless SDK as one AAR, without any UI, published to `sdk/aar/build/repository` (see [Using the SDK](./docs/guides/sdk-integration.md)):

```bash
./gradlew :sdk:aar:publish
```

`:sdk:aar:publish` writes the AAR to `sdk/aar/build/repository`.

Before contributing, read [`CONTRIBUTING.md`](./CONTRIBUTING.md) and [`CODING_STANDARDS.md`](./CODING_STANDARDS.md).

## Release signing

The build uses the `ciRelease` signing configuration only when all four variables are set:

- `ANDROID_KEYSTORE_PATH`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

Without the complete set, the local `release` build is signed with the debug key. Do not treat such an artifact as an official release.

# Privacy and limitations

- Detection is performed on the device. The project does not claim that every modified environment can be detected.
- Network-capable features are limited to the consented TEE revocation refresh, GitHub update metadata and changelog checks (through gh-proxy.com while GitHub acceleration is on), and links explicitly opened by the user.
- Results are heuristic and can contain false positives, false negatives, unsupported checks, or incomplete evidence.
- Hardware-backed KeyStore, StrongBox, Binder behavior, `/proc`, SELinux, mount namespaces, and other low-level interfaces vary by device and OS build.

# Project status

Published builds live in the [releases](https://github.com/KaiZeHuang/vernier/releases).
Build from source with:

```bash
./gradlew :app:assembleDebug      # APK
./gradlew :sdk:aar:publish        # SDK AAR
```

# Disclaimer

This software is provided "as is", without warranty of any kind. It is intended for education, diagnostics, and security research. The developers are not liable for damage, data loss, or system instability resulting from its use. Interpret heuristic findings in the context of the device, OS build, and available probe coverage.
