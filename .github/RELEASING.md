# Release policy

Stable releases are built by `.github/workflows/release-latest.yml` from an existing `v*` tag.
Manual releases must start the workflow from the repository's default branch and name an existing
tag; the workflow never creates a tag from a branch head.

## Trusted source history

A stable release tag must point to a commit reachable from the repository's default branch. This
keeps tag-controlled Gradle and Android build logic inside the same reviewed history that is
allowed to receive the release signing credentials.

## Historical rebuild compatibility

The supported rebuild baseline is the repository's first commit. The workflow derives it with
`git rev-list --max-parents=0` rather than pinning a SHA, so the guard survives forks and history
rewrites; a pinned SHA silently breaks the moment the history is rewritten, which is exactly what
happened when Vernier's history was collapsed into a single initial commit. The selected source
tree always supplies its own pinned toolchain values and Gradle build logic.

The release workflow runs the shared setup action from the workflow revision itself through the
`./` local-action reference, while checking out source from the selected tag.
