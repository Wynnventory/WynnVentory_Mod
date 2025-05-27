"use strict";

function determineVersionBump(commits) {
    // chore(release) or feat(major) -> major (0)
    // feat -> minor (1)
    // otherwise -> patch (2)

    let releaseType = 2;
    for (let commit of commits) {
        if (commit == null || !commit.header) continue;

        // We want to select the highest release type
        if (commit.header.startsWith("chore(release)") || commit.header.startsWith("feat(major)")) {
            releaseType = 0;
            break;
        }

        if (commit.header.startsWith("feat") && releaseType > 1) {
            releaseType = 1;
        }
    }

    let releaseTypes = ["major", "minor", "patch"];

    let reason = "No special commits found. Defaulting to a patch.";

    switch (releaseTypes[releaseType]) {
        case "major":
            reason = "Found a commit with a chore(release) or feat(major) header.";
            break;
        case "minor":
            reason = "Found a commit with a feat! or fix! header.";
            break;
    }

    return {
        releaseType: releaseTypes[releaseType],
        reason: reason
    };
}

async function getOptions() {
    const { default: config } = await import("conventional-changelog-conventionalcommits");

    // Initialize options using the preset
    const options = await config({
        types: [
            // Unhide all types except "ci" so they appear in the changelog
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
        ]
    });

    // Override bumpType to use our custom logic
    options.bumpType = determineVersionBump;

    return options;
}

module.exports = getOptions();
