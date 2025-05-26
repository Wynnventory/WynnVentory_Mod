const fs = require("fs");

exports.preCommit = (props) => {
    console.log(`⚙️  preCommit() props → version: ${props.version}, isPreRelease: ${props.isPreRelease}`);

    const replace = (path, searchValue, replaceValue) => {
        let content = fs.readFileSync(path, "utf-8");
        if (content.match(searchValue)) {
            fs.writeFileSync(path, content.replace(searchValue, replaceValue));
            console.log(`"${path}" changed`);
        }
    };

    let version = props.version;

    // If this is a release workflow (not a pre-release) and the version contains "-dev"
    // Strip the "-dev" suffix from the version
    if (props.isPreRelease === false && version.includes("-dev")) {
        version = version.replace(/-dev\.\d+$/, "");
        console.log(`🔖 Stripping -dev suffix for release. New version: ${version}`);
    }

    // replace only the version string with new version generated from config.json:
    replace("./build.gradle", /(?<=version = ")\d+\.\d+\.\d+((-\w+)+\.\d+)?(?=")/g, version);
};
