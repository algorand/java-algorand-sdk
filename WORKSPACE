
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

RULES_JVM_EXTERNAL_TAG = "2.8"
RULES_JVM_EXTERNAL_SHA = "79c9850690d7614ecdb72d68394f994fef7534b292c4867ce5e7dec0aa7bdfad"

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
    strip_prefix = "bazel-common-413b433b91f26dbe39cdbc20f742ad6555dd1e27",
    sha256 = "d8c9586b24ce4a5513d972668f94b62eb7d705b92405d4bc102131f294751f1d",
    url = "https://github.com/google/bazel-common/archive/413b433b91f26dbe39cdbc20f742ad6555dd1e27.zip",
)
BAZEL_SKYLIB_TAG = "0.6.0"
http_archive(
    name = "bazel_skylib",
    strip_prefix = "bazel-skylib-%s" % BAZEL_SKYLIB_TAG,
    sha256 = "eb5c57e4c12e68c0c20bc774bfbc60a568e800d025557bc4ea022c6479acc867",
    url = "https://github.com/bazelbuild/bazel-skylib/archive/%s.tar.gz" %
          BAZEL_SKYLIB_TAG,
)
