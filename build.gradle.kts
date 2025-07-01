plugins {
  id("java")
}

group = "dev.ultreon.quants"
version = property("mod_version").toString()

repositories {
  mavenCentral()
  maven {
    name = "UltreonMavenReleases"
    url = uri("https://maven.ultreon.dev/releases")
  }
  maven {
    name = "UltreonMavenSnapshots"
    url = uri("https://maven.ultreon.dev/snapshots")
  }
  maven("https://jitpack.io/")
  maven("https://maven.fabricmc.net/")
  maven("https://teavm.org/maven/repository")
  maven("https://oss.sonatype.org/content/repositories/releases")
  maven("https://oss.sonatype.org/content/repositories/snapshots")
  maven("https://repo.glaremasters.me/repository/public/")
}

dependencies {
  // Test
  testImplementation(platform("org.junit:junit-bom:5.10.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")

  implementation("org.jetbrains:annotations:24.0.1")

  // Main source set
  implementation("dev.ultreon.quantum:quantum-server:0.2.0-SNAPSHOT")
  implementation("dev.ultreon.quantum:dedicated:0.2.0-SNAPSHOT")
  implementation("dev.ultreon.quantum:quantum-gameprovider:0.2.0-SNAPSHOT")

  // Client source set
  implementation("dev.ultreon.quantum:quantum-desktop:0.2.0-SNAPSHOT")
  implementation("dev.ultreon.quantum:quantum-client:0.2.0-SNAPSHOT")

  implementation("com.google.guava:guava:32.0.1-jre")
}

mkdir("run")

tasks.register<JavaExec>("runClient") {
  dependsOn("classes")
  mainClass.set("net.fabricmc.loader.impl.launch.knot.KnotClient")
  classpath = sourceSets["main"].runtimeClasspath

  jvmArgs = if (System.getProperty("os.name").contains("Mac"))
    listOf("-XstartOnFirstThread", "-Dfabric.development=true")
  else
    listOf("-Dfabric.development=true")

  workingDir = file("run")
}

tasks.register<JavaExec>("runServer") {
  dependsOn("classes")
  mainClass.set("net.fabricmc.loader.impl.launch.knot.KnotServer")
  classpath = sourceSets["main"].runtimeClasspath
  jvmArgs = listOf("-Dfabric.development=true")
  workingDir = file("run")
}