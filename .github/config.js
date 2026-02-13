"use strict";
const config = require("conventional-changelog-conventionalcommits");

function whatBump(commits) {
    let releaseType = 2;

    // chore(bump-mc) or chore! -> major (0)
    // feat! or fix! -> minor (1)
    // otherwise -> patch (2)

    for (let commit of commits) {
        if (commit == null || !commit.header || commit.header.includes("[skip ci]")) continue;

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
            reason = "Found a commit with a chore(bump-mc) or feat(major) header.";
            break;
        case "minor":
            reason = "Found a commit with a feat! or fix! header.";
            break;
    }

    return {
        releaseType: releaseTypes[releaseType],
        reason: reason
    }
}

async function getOptions() {
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
    options.recommendedBumpOpts.whatBump = whatBump;
    options.whatBump = whatBump;

    return options;
}

module.exports = getOptions();
