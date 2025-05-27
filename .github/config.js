"use strict";
const path   = require("path");
const config = require("conventional-changelog-conventionalcommits");

// load your root package.json to peek at the current version
const pkg = require(path.join(__dirname, "..", "package.json"));

// decide major/minor/patch or prerelease
function determineVersionBump(commits) {
  console.log("🔨 determineVersionBump() called, pkg.version =", pkg.version);

  // if we're already on a dev prerelease, only bump that counter
  if (/-dev\.\d+$/.test(pkg.version)) {
    console.log("   → detected existing -dev.N → using 'prerelease'");
    return { releaseType: "prerelease"
           , reason: "Already in dev prerelease, increment only its counter." };
  }

  // otherwise fall back to major/minor/patch
  let releaseType = 2; // patch
  console.log("   • initial releaseType = patch");

  for (let commit of commits || []) {
    if (!commit || !commit.header) continue;
    console.log(`   • inspecting: ${commit.header}`);

    // major: chore(release) or feat(major)
    if (
      commit.header.startsWith("chore(release)") ||
      commit.header.startsWith("feat(major)")
    ) {
      console.log("     → matched chore(release)/feat(major) → major");
      releaseType = 0;
      break;
    }

    // minor: feat! or fix!
    if (
      (commit.header.startsWith("feat!") || commit.header.startsWith("fix!"))
      && releaseType > 1
    ) {
      console.log("     → matched feat! or fix! → minor");
      releaseType = 1;
    }
  }

  const types = ["major","minor","patch"];
  const chosen = types[releaseType];
  const reason = {
    major: "Found chore(release) or feat(major).",
    minor: "Found feat! or fix!.",
    patch: "Defaulting to patch."
  }[chosen];

  console.log(`   • final decision → ${chosen} (${reason})`);
  return { releaseType: chosen, reason };
}

// --- synchronous export of the actual options object ---
module.exports = config({
  types: [
    { type: "feat",    section: "New Features"             },
    { type: "feature", section: "New Features"             },
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
  ],
  bumpType: determineVersionBump
});
