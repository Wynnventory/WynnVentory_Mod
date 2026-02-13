"use strict";

function whatBump(commits) {
    let releaseType = 2;

    // Is this a development build?
    const isDev = process.env.IS_DEV === 'true';

    if (isDev) {
        // In development mode, we always want a 'patch' bump at most,
        // which combined with pre-release: true will just increment the N in dev.N
        // if the current version is already a pre-release.
        // Actually, returning patch is fine because TriPSs action handles the pre-release logic.
        releaseType = 2; 
    }

    for (let commit of commits) {
        if (commit == null || !commit.header || commit.header.includes("[skip ci]")) continue;

        if (commit.header.startsWith("chore(release)") || commit.header.startsWith("feat(major)") || commit.header.includes("BREAKING CHANGE") || commit.header.includes("!")) {
            if (!isDev) {
                releaseType = 0;
                break;
            }
        }

        if (commit.header.startsWith("feat") && releaseType > 1) {
            if (!isDev) {
                releaseType = 1;
            }
        }
    }

    let releaseTypes = ["major", "minor", "patch"];
    let reason = isDev ? "Development build: forcing patch bump to only increment dev.N suffix." : "No special commits found. Defaulting to a patch.";

    if (!isDev) {
        switch (releaseTypes[releaseType]) {
            case "major":
                reason = "Found a commit with a chore(release), feat(major) header, or breaking change.";
                break;
            case "minor":
                reason = "Found a commit with a feat header.";
                break;
        }
    }

    return {
        level: releaseType, // conventional-recommended-bump uses 'level' (0, 1, 2)
        reason: reason
    }
}

async function getOptions() {
    const { default: config } = await import("conventional-changelog-conventionalcommits");
    let options = await config(
        {
            types: [
                { type: "feat", section: "New Features" },
                { type: "feature", section: "New Features" },
                { type: "fix", section: "Bug Fixes" },
                { type: "perf", section: "Performance Improvements" },
                { type: "revert", section: "Reverts" },
                { type: "docs", section: "Documentation", hidden: true },
                { type: "style", section: "Styles", hidden: true },
                { type: "chore", section: "Miscellaneous Chores", hidden: true },
                { type: "refactor", section: "Code Refactoring", hidden: true },
                { type: "test", section: "Tests", hidden: true },
                { type: "build", section: "Build System", hidden: true },
                { type: "ci", section: "Continuous Integration", hidden: true },
            ]
        }
    );

    // Both of these are used in different places...
    if (!options.recommendedBumpOpts) options.recommendedBumpOpts = {};
    options.recommendedBumpOpts.whatBump = whatBump;
    options.whatBump = whatBump;

    return options;
}

module.exports = getOptions();
