plugins {
    id("com.falsepattern.fpgradle-mc") version "3.3.0"
	id("org.gradlex.reproducible-builds") version "1.1"
}

group = "gayhearts"

minecraft_fp {
    mod {
        modid   = "OCSedna"
        name    = "OpenComputers Sedna"
        rootPkg = "$group.ocsedna"
    }

    tokens {
        tokenClass = "Tags"
    }

	/* Figure out how to limit this to this mod's code, and not dependencies. */
	tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
    }
}

repositories {
	maven(url = "https://maven.minecraftforge.net/")
    exclusive(horizon(), "com.github.GTNewHorizons")
}

dependencies {
    compileOnly("com.github.GTNewHorizons:OpenComputers:1.12.8-GTNH:api") {
        excludeDeps()
    }
    runtimeOnly("com.github.GTNewHorizons:OpenComputers:1.12.8-GTNH:dev") {
        excludeDeps()
    }

	implementation("net.minecraftforge.gradle:ForgeGradle:2.1-SNAPSHOT")

}
