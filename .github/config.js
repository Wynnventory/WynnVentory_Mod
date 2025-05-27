"use strict";
const semver = require("semver");
const preset = require("conventional-changelog-conventionalcommits");

/**
 * Determine the semantic release bump, handling pre-releases with the "dev" identifier.
 * @param {Array} commits - List of commit objects with headers
 * @param {number} _releaseCount - Number of previous releases (unused)
 * @param {Object} context - Context object containing lastRelease.version
 * @returns {{releaseType: string, reason: string}}
 */
function determineVersionBump(commits, _releaseCount, context) {
    const lastVersion = context.lastRelease?.version;
    // If the last release was already a dev prerelease, bump just the prerelease counter
    if (lastVersion) {
        const pre = semver.prerelease(lastVersion);
        if (pre && pre[0] === "dev") {
            console.log(`Previous release was a dev prerelease (${lastVersion}), bumping dev counter`);
            return {
                releaseType: "prerelease",
                reason: `Previous was prerelease (${lastVersion}), bumping dev counter`,
            };
        }
    }

    // Otherwise apply normal semver rules: chore(release)/feat(major) -> major; feat -> minor; else patch
    let releaseTypeIndex = 2; // default patch
    for (const commit of commits) {
        if (!commit || !commit.header) continue;
        if (
            commit.header.startsWith("chore(release)") ||
            commit.header.startsWith("feat(major)")
        ) {
            releaseTypeIndex = 0;
            break;
        }
        if (
            commit.header.startsWith("feat") &&
            releaseTypeIndex > 1
        ) {
            releaseTypeIndex = 1;
        }
    }

    const releaseTypes = ["major", "minor", "patch"];
    let reason = "No special commits found. Defaulting to patch.";
    switch (releaseTypes[releaseTypeIndex]) {
        case "major":
            reason = "Found chore(release) or feat(major).";
            break;
        case "minor":
            reason = "Found feat commit.";
            break;
    }

    return {
        releaseType: releaseTypes[releaseTypeIndex],
        reason: reason
    };
}

/**
 * Exported config function for conventional-changelog-action
 */
async function getOptions() {
    const options = await preset({
        types: [
            { type: "feat", section: "New Features" },
            { type: "feature", section: "New Features" },
            { type: "fix", section: "Bug Fixes" },
            { type: "perf", section: "Performance Improvements" },
            { type: "refactor", section: "Code Refactoring" },
            { type: "revert", section: "Reverts" },
            { type: "style", section: "Styles", hidden: true },
            { type: "docs", section: "Documentation", hidden: true },
            { type: "chore", section: "Miscellaneous Chores", hidden: true },
            { type: "test", section: "Tests", hidden: true },
            { type: "build", section: "Build System", hidden: true },
            { type: "ci", section: "Continuous Integration", hidden: true },
        ],
    });

    options.bumpType = determineVersionBump;
    return options;
}

module.exports = getOptions();
