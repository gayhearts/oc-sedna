// Minecraft info.
var minecraft_version = extra["minecraft_version"]
var minecraft_sdk     = extra["minecraft_sdk"]

// Dependency version info.
var opencomputers_version = extra["opencomputers_version"]

var ceres_version         = extra["ceres_version"]

var sedna_version         = extra["sedna_version"]
var sedna_commit_ref      = extra["sedna_commit_ref"]

var minux_version         = extra["minux_version"]

// Repositories for dependencies.
var ceres_repo = extra["ceres_repo"]
var sedna_repo = extra["sedna_repo"]
var minux_repo = extra["minux_repo"]

plugins {
	id("org.gradlex.reproducible-builds") version "1.1"
	id("com.gtnewhorizons.retrofuturagradle") version "1.4.0"
	kotlin("jvm") version "2.3.0"
}

group = "gayhearts"

kotlin {
	jvmToolchain(17)
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

repositories {
	maven("https://maven.minecraftforge.net/")
	maven("https://cursemaven.com")

	maven {
		name = "GTNH Maven"
		url = uri("https://nexus.gtnewhorizons.com/repository/public/")
	}

	mavenCentral()
	listOf("${sedna_repo}", "${minux_repo}", "${ceres_repo}").forEach{repo ->
		maven {
			url = uri("https://maven.pkg.github.com/${repo}")
			credentials {
				username = project.findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")
				password = project.findProperty("gpr.key") as String? ?: System.getenv("GPR_KEY")			}
			}
		}
	}

	dependencies {
		implementation("com.github.GTNewHorizons:OpenComputers:${opencomputers_version}:api")
		implementation("com.github.GTNewHorizons:OpenComputers:${opencomputers_version}:dev")

		implementation("li.cil.ceres:ceres:${ceres_version}")
		implementation("li.cil.sedna:sedna:${sedna_version}")
		implementation("li.cil.sedna:sedna-buildroot:${minux_version}")
	}

	minecraft {
		mcVersion.set("${minecraft_version}")

		// Generate a field named VERSION with the mod version in the injected Tags class
		injectedTags.put("VERSION", project.version)

		// If you need the old replaceIn mechanism, prefer the injectTags task because it doesn't inject a javac plugin.
		// tagReplacementFiles.add("RfgExampleMod.java")

		// Enable assertions in the mod's package when running the client or server
		extraRunJvmArguments.add("-ea:${project.group}")

		// If needed, add extra tweaker classes like for mixins.
		//extraTweakClasses.add("org.spongepowered.asm.launch.MixinTweaker")

		// Exclude some Maven dependency groups from being automatically included in the reobfuscated runs
		//groupsToExcludeFromAutoReobfMapping.addAll("", "")

	}
