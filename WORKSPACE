
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

RULES_JVM_EXTERNAL_TAG = "1.2"
RULES_JVM_EXTERNAL_SHA = "e5c68b87f750309a79f59c2b69ead5c3221ffa54ff9496306937bfa1c9c8c86b"

http_archive(
    name = "rules_jvm_external",
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    sha256 = RULES_JVM_EXTERNAL_SHA,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

# Maven dependencies
load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    name = "maven",
        artifacts = [
            # Crypto (for tests and a util function. Clients should provide their own provider).
            "org.bouncycastle:bcprov-jdk15on:1.61",
            # Encoding
            "org.msgpack:jackson-dataformat-msgpack:0.8.16",
            "commons-codec:commons-codec:1.12",
            # Tests
            "junit:junit:4.12",
        ],
        repositories = [
            "https://repo1.maven.org/maven2",
            "https://maven.google.com",
        ],
        fetch_sources = False,
)

maven_install(
    name = "mavenjackson",
    artifacts = [
        # Encoding - avoid pulling in transitive versions of jackson
        "com.fasterxml.jackson.core:jackson-core:2.7.9", # 2.7 for android min16
        "com.fasterxml.jackson.core:jackson-annotations:2.7.9",
        "com.fasterxml.jackson.core:jackson-databind:2.7.9",
    ],
    repositories = [
        "https://repo1.maven.org/maven2",
        "https://maven.google.com",
    ],
    fetch_sources = False,
)

maven_install(
    name = "mavenforswagger",
    artifacts = [
        # Swagger-Generated Client API
        "io.swagger:swagger-annotations:1.5.18",
        "com.squareup.okhttp:okhttp:2.7.5",
        "com.squareup.okhttp:logging-interceptor:2.7.5",
        "com.squareup.okio:okio:1.6.0",
        "com.google.code.gson:gson:2.8.1",
        "io.gsonfire:gson-fire:1.8.0",
        "org.threeten:threetenbp:1.3.5",
        "org.apache.commons:commons-lang3:3.6",
    ],
    repositories = [
        "https://repo1.maven.org/maven2",
        "https://maven.google.com",
    ],
    fetch_sources = False,
)

# for pom_file, maven integration
http_archive(
    name = "bazel_common",
    strip_prefix = "bazel-common-f1115e0f777f08c3cdb115526c4e663005bec69b",
    sha256 = "1e05a4791cc3470d3ecf7edb556f796b1d340359f1c4d293f175d4d0946cf84c",
    url = "https://github.com/google/bazel-common/archive/f1115e0f777f08c3cdb115526c4e663005bec69b.zip",
)
BAZEL_SKYLIB_TAG = "0.6.0"
http_archive(
    name = "bazel_skylib",
    strip_prefix = "bazel-skylib-%s" % BAZEL_SKYLIB_TAG,
    sha256 = "eb5c57e4c12e68c0c20bc774bfbc60a568e800d025557bc4ea022c6479acc867",
    url = "https://github.com/bazelbuild/bazel-skylib/archive/%s.tar.gz" %
          BAZEL_SKYLIB_TAG,
)
