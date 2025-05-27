"use strict";
const path   = require("path");
const config = require("conventional-changelog-conventionalcommits");

// Resolve the path to your project-root package.json
const pkgPath = path.join(__dirname, "..", "package.json");
console.log(`🔍 Loading package.json from: ${pkgPath}`);
const pkg = require(pkgPath);
console.log(`✅ Current version in package.json: ${pkg.version}`);

function determineVersionBump(commits) {
  console.log("🔨 determineVersionBump(): called");

    // 1) If we're already on a dev prerelease, bump only the prerelease counter
    if (/-dev\.\d+$/.test(pkg.version)) {
        console.log("   → Detected existing -dev.N prerelease → returning 'prerelease'");
        return {
          releaseType: "prerelease",
          reason: "Already on dev prerelease, only bumping its counter."
        };
    }
    
    let releaseType = 2;
    console.log(`   • Initial releaseType = patch (2)`);

    for (let commit of commits) {
        if (!commit || !commit.header) continue;
        console.log(`   • Inspecting commit.header: "${commit.header}"`);

        // chore(release) or feat(major)! -> major (0)
        if (commit.header.startsWith("chore(release)") || commit.header.startsWith("feat(major)")
        ) {
            console.log("     → matched chore(release) or feat(major) → major bump");
            releaseType = 0;
            break;
        }

        // feature commit -> minor (1)
        if (commit.header.startsWith("feat") && releaseType > 1) {
            console.log("     → matched feat → minor bump (if not already set)");
            releaseType = 1;
        }
    }

    const releaseTypes = ["major", "minor", "patch"];
    const chosen = releaseTypes[releaseType];
    let reason = "No special commits found. Defaulting to a patch.";

    switch (chosen) {
        case "major":
            reason = "Found a chore(release) or feat(major) commit.";
            break;
        case "minor":
            reason = "Found a feat commit.";
            break;
    }

    console.log(`   • Final decision → releaseType="${chosen}", reason="${reason}"`);
    return { releaseType: chosen, reason };
}

async function getOptions() {
    console.log("🚀 getOptions(): initializing conventional-changelog options…");
    const options = await config({
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

    console.log("🔧 getOptions(): attaching custom bumpType function");
    options.bumpType = determineVersionBump;

    return options;
}

module.exports = getOptions();
