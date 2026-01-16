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
	listOf("North-Western-Development/sedna", "North-Western-Development/minux", "fnuecke/ceres").forEach{repo ->
		maven {
			url = uri("https://maven.pkg.github.com/${repo}")
			credentials {
				username = project.findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")
				password = project.findProperty("gpr.key") as String? ?: System.getenv("GPR_KEY")			}
			}
		}
	}

	dependencies {
		implementation("com.github.GTNewHorizons:OpenComputers:1.12.8-GTNH:api")
		implementation("com.github.GTNewHorizons:OpenComputers:1.12.8-GTNH:dev")

		implementation("li.cil.ceres:ceres:0.0.4")
		implementation("li.cil.sedna:sedna:2.0.13")
		implementation("li.cil.sedna:sedna-buildroot:0.0.64")
		implementation("curse.maven:oc2r-1037738:6280699")
	}

	minecraft {
		mcVersion.set("1.7.10")

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
