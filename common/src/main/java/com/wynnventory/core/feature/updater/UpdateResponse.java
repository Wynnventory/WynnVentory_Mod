package com.wynnventory.core.feature.updater;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateResponse {
    @JsonProperty("game_versions")
    public List<String> gameVersions;

    @JsonProperty("loaders")
    public List<String> loaders;

    @JsonProperty("id")
    public String id;

    @JsonProperty("project_id")
    public String projectId;

    @JsonProperty("author_id")
    public String authorId;

    @JsonProperty("featured")
    public boolean featured;

    @JsonProperty("name")
    public String name;

    @JsonProperty("version_number")
    public String versionNumber;

    @JsonProperty("changelog")
    public String changelog;

    @JsonProperty("changelog_url")
    public Object changelogUrl;

    @JsonProperty("date_published")
    public Date datePublished;

    @JsonProperty("downloads")
    public int downloads;

    @JsonProperty("version_type")
    public String versionType;

    @JsonProperty("status")
    public String status;

    @JsonProperty("requested_status")
    public Object requestedStatus;

    @JsonProperty("files")
    public List<Artifact> files;

    @JsonProperty("dependencies")
    public List<ModDependency> dependencies;
}

class ModDependency {
    @JsonProperty("version_id")
    public Object versionId;

    @JsonProperty("project_id")
    public String projectId;

    @JsonProperty("file_name")
    public Object fileName;

    @JsonProperty("dependency_type")
    public String dependencyType;
}

class Artifact {
    @JsonProperty("id")
    public String id;

    @JsonProperty("hashes")
    public Hashes hashes;

    @JsonProperty("url")
    public String url;

    @JsonProperty("filename")
    public String filename;

    @JsonProperty("primary")
    public boolean primary;

    @JsonProperty("size")
    public int size;

    @JsonProperty("file_type")
    public Object fileType;
}

class Hashes {
    @JsonProperty("sha512")
    public String sha512;

    @JsonProperty("sha1")
    public String sha1;
}
