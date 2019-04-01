load("@bazel_tools//tools/build_defs/repo:java.bzl", "java_import_external")

# External dependencies
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
http_archive(
    name = "rules_jvm_external",
    strip_prefix = "rules_jvm_external-1.1",
    sha256 = "ade316ec98ba0769bb1189b345d9877de99dd1b1e82f5a649d6ccbcb8da51c1f",
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/1.1.zip"
)

# Maven dependencies
load("@rules_jvm_external//:defs.bzl", "maven_install")
maven_install(
    name = "maven",
    artifacts = [
        # Crypto
        "org.bouncycastle:bcprov-jdk15on:1.61",
        # Encoding
        "com.fasterxml.jackson.core:jackson-core:2.7.9", # 2.7 for android
        "com.fasterxml.jackson.core:jackson-annotations:2.7.0",
        "com.fasterxml.jackson.core:jackson-databind:2.7.9",
        "org.msgpack:msgpack-core:0.8.16",
        "org.msgpack:jackson-dataformat-msgpack:0.8.16",
        "commons-codec:commons-codec:1.12",
        # Client API
        "com.squareup.retrofit2:retrofit:2.5.0",
        "com.google.code.gson:gson:2.8.5",
        "org.apache.commons:commons-lang3:3.8.1",
        "com.squareup.okhttp3:okhttp:3.14.0",
        "com.squareup.retrofit2:converter-gson:2.5.0",
        # Tests
        "junit:junit:4.12",
    ],
    repositories = [
        "https://repo1.maven.org/maven2",
        "https://maven.google.com",
    ],
    fetch_sources = False,
)

# TODO: consider some checksum verification esp. for bouncycastle
