# Security Policy

Vernier inspects the device it runs on. That is its whole job, and it is also the
reason this project takes its own attack surface seriously.

## Supported Versions

Only the latest published release receives security fixes. Vernier is currently
in alpha; treat every build as untested in production.

| Version | Supported |
| --- | --- |
| Latest release | Yes |
| `nightly` | Best effort, no guarantee |
| Anything older | No |

## Reporting a Vulnerability

**Please do not report security vulnerabilities through public GitHub issues.**

Report them privately instead, so that a fix can ship before the details are
public:

1. Go to <https://github.com/KaiZeHuang/vernier/security/advisories/new>.
2. Or email **kaizehuang@users.noreply.github.com**.

Please include:

- A description of the vulnerability and its impact.
- The affected component or module (for example `:feature:tee:data`).
- Steps to reproduce, or a proof of concept.
- The version, commit SHA, or release tag you tested against.
- Any suggested remediation, if you have one.

## What to Expect

- **Acknowledgement** within 3 business days.
- **An initial assessment** within 7 business days, including whether the report
  is accepted and how severe we consider it.
- **A fix or mitigation plan** with a target release, for accepted reports.
- **Credit in the release notes and the GitHub advisory**, unless you prefer to
  stay anonymous. Say so in your report and we will honour it.

We follow coordinated disclosure: we ask that you give us a reasonable window —
90 days from acknowledgement, or 30 days after a fix is published, whichever
comes first — before publishing details.

## Scope

### In scope

- The APK and the SDK AAR published from this repository.
- The build and release pipelines in `.github/workflows/`.
- The CI guard scripts in `.github/scripts/`, since they are what keeps the
  native and JNI boundaries honest.
- Privilege or integrity problems in Vernier's own probes: a detector that can
  be tricked into reporting "all clear", or that crashes or hangs the host
  process.

### Out of scope

- Vulnerabilities in the Android platform, in the kernel, or in other apps that
  Vernier merely reports on. Report those upstream.
- Anything requiring a rooted device, a custom ROM, or physical access to an
  already-compromised device as a precondition.
- Social engineering of maintainers.

## A Note on What Vernier Reports

Vernier is a **diagnostic** tool. A detector reporting an accurate finding is
working as intended, even when the finding is unflattering. A detector that can
be made to report the opposite of the truth is a bug in Vernier — please report
it.
