"use strict";

const semver = require("semver");
const preset = require("conventional-changelog-conventionalcommits");

console.log("[config.js] Loaded custom conventional-changelog config");

/**
 * Decide how to bump based on commits and previous release
 * @param {import('conventional-changelog-core').Commit[]} commits
 * @param {number} _releaseCount
 * @param {{ lastRelease: { version?: string } }} context
 */
function determineVersionBump(commits, _releaseCount, context) {
    console.log("[config.js] determineVersionBump called", {
        commitCount: commits.length,
        lastRelease: context.lastRelease?.version
    });

    const last = context.lastRelease?.version;
    // If last release is a dev prerelease, bump prerelease counter
    if (last) {
        const pre = semver.prerelease(last);
        console.log("[config.js] semver.prerelease(last) =>", pre);
        if (pre && pre[0] === "dev") {
            console.log(`[config.js] Last release was dev prerelease (${last}), returning prerelease bump`);
            return {
                releaseType: "prerelease",
                reason: `Previous was prerelease (${last}), bumping dev counter`,
            };
        }
    }

    // Fallback to normal semver rules
    let idx = 2;
    for (const c of commits) {
        console.log("[config.js] commit header:", c.header);
        if (!c.header) continue;
        if (c.header.startsWith("chore(release)") || c.header.startsWith("feat(major)")) {
            idx = 0;
            break;
        }
        if (c.header.startsWith("feat") && idx > 1) {
            idx = 1;
        }
    }
    const types = ["major", "minor", "patch"];
    const chosen = types[idx];
    console.log(`[config.js] Normal bump logic chose '${chosen}'`);
    const reasonMap = {
        major: "Found chore(release) or feat(major)",
        minor: "Found feat commit",
        patch: "Default to patch",
    };

    return { releaseType: chosen, reason: reasonMap[chosen] };
}

// Build preset synchronously
const options = preset({
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
console.log("[config.js] Custom bumpType assigned");

module.exports = options;