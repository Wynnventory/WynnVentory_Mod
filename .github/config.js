// config.js
"use strict";
const config = require("conventional-changelog-conventionalcommits");
const path        = require("path");
const versionInfo = require(path.join(__dirname, "..", "version.json"));

function determineVersionBump(commits) {
    //  ─── if we're already on a dev prerelease, just bump that ──────────────
    if (/-dev\.\d+$/.test(pkg.version)) {
        return "prerelease";
    }

    // ─── otherwise fall back to your existing major/minor/patch logic ───────
    let releaseType = 2;
    for (let commit of commits) {
        if (!commit || !commit.header) continue;
        if (commit.header.startsWith("chore(release)")
            || commit.header.startsWith("feat(major)")) {
            releaseType = 0; break;
        }
        if (commit.header.startsWith("feat") && releaseType > 1) {
            releaseType = 1;
        }
    }

    return ["major", "minor", "patch"][releaseType];
}

async function getOptions() {
    console.log("Getting options...");
    let options = await config(
        {
            types: [
                { type: "feat", section: "New Features" },
                { type: "feature", section: "New Features" },
                { type: "fix", section: "Bug Fixes" },
                { type: "perf", section: "Performance Improvements" },
                { type: "revert", section: "Reverts" },
                { type: "docs", section: "Documentation" },
                { type: "style", section: "Styles" },
                { type: "refactor", section: "Code Refactoring" },
                { type: "test", section: "Tests" },
                { type: "build", section: "Build System" },
                { type: "chore", section: "Miscellaneous Chores", hidden: true },
                { type: "ci", section: "Continuous Integration", hidden: true },
            ]
    });

    // Both of these are used in different places...
    options.bumpType = determineVersionBump;

    return options;
}

module.exports = getOptions();
