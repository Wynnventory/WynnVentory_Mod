"use strict";
const path   = require("path");
const fs     = require("fs");
const config = require("conventional-changelog-conventionalcommits");

// Resolve the path to your root-level version.json
const versionPath = path.join(__dirname, "..", "version.json");
console.log(`🔍 Loading version.json from: ${versionPath}`);

// Read and log the raw file contents
let raw = "";
try {
  raw = fs.readFileSync(versionPath, "utf8");
  console.log("📄 version.json raw content:");
  console.log(raw);
} catch (err) {
  console.error(`❌ Failed to read version.json: ${err.message}`);
}

// Attempt JSON.parse on that raw content
let versionInfo = { version: "unknown" };
try {
  versionInfo = JSON.parse(raw);
  console.log(`✅ Parsed version.json → version = "${versionInfo.version}"`);
} catch (err) {
  console.error(`❌ Failed to parse version.json: ${err.message}`);
}

function determineVersionBump(commits) {
  console.log("🔨 determineVersionBump() called");
  console.log(`   • Current version: ${versionInfo.version}`);

  let releaseType = 2; // default to patch
  for (let commit of commits || []) {
    if (!commit || !commit.header) continue;
    console.log(`   • Inspecting commit header: "${commit.header}"`);

    if (
      commit.header.startsWith("chore(release)") ||
      commit.header.startsWith("feat(major)")
    ) {
      console.log("     → Matched chore(release) or feat(major) → major bump");
      releaseType = 0;
      break;
    }
    if (commit.header.startsWith("feat") && releaseType > 1) {
      console.log("     → Matched feat → minor bump (if not already set)");
      releaseType = 1;
    }
  }

  const releaseTypes = ["major", "minor", "patch"];
  let reason = "No special commits found. Defaulting to a patch.";
  switch (releaseTypes[releaseType]) {
    case "major":
      reason = "Found a commit with a chore(release) or feat(major) header.";
      break;
    case "minor":
      reason = "Found a commit with a feat! or fix! header.";
      break;
  }

  console.log(
    `   • Final decision → releaseType="${releaseTypes[releaseType]}", reason="${reason}"`
  );
  return {
    releaseType: releaseTypes[releaseType],
    reason,
  };
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
    ],
  });

  console.log("🔧 Plugging in custom bumpType function");
  options.bumpType = determineVersionBump;
  return options;
}

module.exports = getOptions();
