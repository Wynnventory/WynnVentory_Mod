"use strict";
const path   = require("path");
const config = require("conventional-changelog-conventionalcommits");

// ─── load your root-level package.json ──────────────────────────────────────
const pkgPath = path.join(__dirname, "..", "package.json");
console.log(`🔍 Loading package.json from: ${pkgPath}`);
const pkg = require(pkgPath);
console.log(`✅ Current version in package.json: ${pkg.version}`);

// ─── decide what kind of bump we need ──────────────────────────────────────
function determineVersionBump(commits) {
  console.log("🔨 determineVersionBump(): called");

  // 1) If already on a dev prerelease → bump the prerelease counter only
  if (/-dev\.\d+$/.test(pkg.version)) {
    console.log("   → Detected existing -dev.N prerelease → returning 'prerelease'");
    return { releaseType: "prerelease", reason: "Bumping existing dev prerelease." };
  }

  // 2) Otherwise scan commits for the highest bump needed
  let releaseType = 2;  // default to patch
  console.log("   • Initial releaseType = patch (2)");

  for (let commit of commits || []) {
    if (!commit || !commit.header) continue;
    console.log(`   • Inspecting commit.header: "${commit.header}"`);

    // major: chore(release) or feat(major)
    if (
      commit.header.startsWith("chore(release)") ||
      commit.header.startsWith("feat(major)")
    ) {
      console.log("     → matched chore(release) or feat(major) → major bump");
      releaseType = 0;
      break;
    }

    // minor: feat! or fix!
    if (
      (commit.header.startsWith("feat") || commit.header.startsWith("fix")) &&
      releaseType > 1
    ) {
      console.log("     → matched feat! or fix! → minor bump");
      releaseType = 1;
    }
  }

  const releaseTypes = ["major", "minor", "patch"];
  const chosen       = releaseTypes[releaseType];
  let reason         = "No special commits found. Defaulting to a patch.";

  if (chosen === "major") {
    reason = "Found chore(release) or feat(major) commit.";
  } else if (chosen === "minor") {
    reason = "Found feat! or fix! commit.";
  }

  console.log(`   • Final decision → releaseType="${chosen}", reason="${reason}"`);
  return { releaseType: chosen, reason };
}

async function getOptions() {
  console.log("🚀 getOptions(): initializing conventional-changelog options…");
  const options = await config({
    types: [
      { type: "feat",    section: "New Features"             },
      { type: "fix",     section: "Bug Fixes"                },
      { type: "perf",    section: "Performance Improvements" },
      { type: "revert",  section: "Reverts"                  },
      { type: "docs",    section: "Documentation"            },
      { type: "style",   section: "Styles"                   },
      { type: "refactor",section: "Code Refactoring"         },
      { type: "test",    section: "Tests"                    },
      { type: "build",   section: "Build System"             },
      { type: "chore",   section: "Miscellaneous Chores", hidden: true },
      { type: "ci",      section: "Continuous Integration", hidden: true },
    ]
  });

  console.log("🔧 getOptions(): attaching custom bumpType function");
  options.bumpType = determineVersionBump;
  return options;
}

module.exports = getOptions();
