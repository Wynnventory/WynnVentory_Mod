"use strict";
const path = require("path");
const fs = require("fs");
const config = require("conventional-changelog-conventionalcommits");

// ─── Load & log version.json ────────────────────────────────────────────────
const versionPath = path.join(__dirname, "..", "version.json");
console.log(`🔍 Loading version.json from: ${versionPath}`);

let raw, versionInfo;
try {
    raw = fs.readFileSync(versionPath, "utf8");
    console.log("📄 version.json raw content:\n", raw);
    versionInfo = JSON.parse(raw);
    console.log(`✅ Parsed version.json → version = "${versionInfo.version}"`);
} catch (err) {
    console.error(`❌ Error reading/parsing version.json: ${err.message}`);
    versionInfo = {version: ""};
}

// ─── decide bump ────────────────────────────────────────────────────────────
function determineVersionBump(commits) {
    console.log("🔨 determineVersionBump() called");
    console.log(`   • Current version: ${versionInfo.version}`);

    // 1) If we’re already on a dev prerelease, just bump that
    if (/-dev\.\d+$/.test(versionInfo.version)) {
        console.log("   → Detected existing -dev.N prerelease → returning prerelease bump");
        return {
            releaseType: "prerelease",
            reason: "Already on a dev prerelease, bump only the prerelease counter."
        };
    }

    // 2) Otherwise use your original major/minor/patch logic
    let releaseType = 2;  // default → patch
    for (let commit of commits || []) {
        if (!commit || !commit.header) continue;
        console.log(`   • Inspecting commit: "${commit.header}"`);

        if (
            commit.header.startsWith("chore(release)") ||
            commit.header.startsWith("feat(major)")
        ) {
            console.log("     → matched chore(release)/feat(major) → major bump");
            releaseType = 0;
            break;
        }
        if (commit.header.startsWith("feat") && releaseType > 1) {
            console.log("     → matched feat → minor bump (if not already set)");
            releaseType = 1;
        }
    }

    const releaseTypes = ["major", "minor", "patch"];
    const choice = releaseTypes[releaseType];
    let reason = "No special commits found. Defaulting to a patch.";

    if (choice === "major") {
        reason = "Found a chore(release) or feat(major) commit.";
    } else if (choice === "minor") {
        reason = "Found a feat commit.";
    }

    console.log(`   → Final decision: ${choice} (${reason})`);
    return {releaseType: choice, reason};
}

async function getOptions() {
    console.log("🚀 getOptions(): initializing conventional-changelog…");
    const options = await config({
        types: [
            {type: "feat", section: "New Features"},
            {type: "feature", section: "New Features"},
            {type: "fix", section: "Bug Fixes"},
            {type: "perf", section: "Performance Improvements"},
            {type: "revert", section: "Reverts"},
            {type: "docs", section: "Documentation"},
            {type: "style", section: "Styles"},
            {type: "refactor", section: "Code Refactoring"},
            {type: "test", section: "Tests"},
            {type: "build", section: "Build System"},
            {type: "chore", section: "Miscellaneous Chores", hidden: true},
            {type: "ci", section: "Continuous Integration", hidden: true},
        ]
    });

    console.log("🔧 Attaching custom bumpType function");
    options.bumpType = determineVersionBump;

    return options;
}

module.exports = getOptions();
