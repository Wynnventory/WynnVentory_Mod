"use strict";

const semver = require("semver");
const preset = require("conventional-changelog-conventionalcommits");

/**
 * @param {import('conventional-changelog-core').Commit[]} commits
 * @param {number} _releaseCount
 * @param {{ lastRelease: { version?: string } }} context
 * @returns {{ releaseType: string; reason: string }}
 */
function determineVersionBump(commits, _releaseCount, context) {
    const last = context.lastRelease?.version;
    // If the last tag is already a dev prerelease, bump only that counter:
    if (last && semver.prerelease(last)?.[0] === "dev") {
        console.log(`Last release was a dev prerelease, bumping dev counter`);
        return {
            releaseType: "prerelease",
            reason: `Previous was prerelease (${last}), bumping dev counter`,
        };
    }

    // Otherwise fall back to your normal semver rules:
    let idx = 2; // 0=major, 1=minor, 2=patch
    for (const c of commits) {
        if (!c.header) continue;
        if (
            c.header.startsWith("chore(release)") ||
            c.header.startsWith("feat(major)")
        ) {
            idx = 0;
            break;
        }
        if (c.header.startsWith("feat") && idx > 1) {
            idx = 1;
        }
    }

    const types = ["major", "minor", "patch"];
    const chosen = types[idx];
    const reasonMap = {
        major: "Found chore(release) or feat(major).",
        minor: "Found feat commit.",
        patch: "Default to patch.",
    };
    return {releaseType: chosen, reason: reasonMap[chosen]};
}

/**
 * Exported config function for conventional-changelog-action
 */
const options = preset({
    types: [
        {type: "feat", section: "New Features"},
        {type: "feature", section: "New Features"},
        {type: "fix", section: "Bug Fixes"},
        {type: "perf", section: "Performance Improvements"},
        {type: "refactor", section: "Code Refactoring"},
        {type: "revert", section: "Reverts"},
        {type: "style", section: "Styles", hidden: true},
        {type: "docs", section: "Documentation", hidden: true},
        {type: "chore", section: "Miscellaneous Chores", hidden: true},
        {type: "test", section: "Tests", hidden: true},
        {type: "build", section: "Build System", hidden: true},
        {type: "ci", section: "Continuous Integration", hidden: true},
    ],
});

options.bumpType = determineVersionBump;

module.exports = options;