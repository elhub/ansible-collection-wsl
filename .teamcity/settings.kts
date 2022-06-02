import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext
import jetbrains.buildServer.configs.kotlin.v2019_2.project
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.version
import no.elhub.common.build.configuration.AutoRelease
import no.elhub.common.build.configuration.CodeReview
import no.elhub.common.build.configuration.ProjectType.ANSIBLE
import no.elhub.common.build.configuration.SonarScan
import no.elhub.common.build.configuration.UnitTest
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.VcsRoot
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

version = "2022.04"

project {

    val projectId = "no.elhub.devxp:ansible-collection-wsl"
    val projectType = ANSIBLE

    params {
//        param("teamcity.ui.settings.readOnly", "true")
    }

    val sonarScanConfig = SonarScan.Config(
        vcsRoot = DslContext.settingsRoot,
        type = projectType,
        sonarId = projectId
    )

    val sonarScan = SonarScan(sonarScanConfig) {
        vcs {
            branchFilter = """
                -:*
                +:<default>
            """.trimIndent()
            cleanCheckout = true
        }
    }

    val release = AutoRelease(
        AutoRelease.Config(
            vcsRoot = DslContext.settingsRoot,
            type = projectType
        )
    ) {
        vcs {
            branchFilter = """
                -:*
                +:<default>
            """.trimIndent()
            cleanCheckout = true
        }

        dependencies {
            snapshot(sonarScan) { }
        }

        triggers {
            vcs {
                branchFilter = """
                    -:*
                    +:<default>
                """.trimIndent()

                quietPeriodMode = VcsTrigger.QuietPeriodMode.USE_DEFAULT
            }
        }
    }

    listOf("Adr", "Ansible", "Arcanist", "Base", "Docker", "Git", "GitUtils", "Java", "Kotlin", "Linters", "Molecule",
        "Node", "Python").forEach { role ->
            buildType(MoleculeTest(MoleculeTest.Config(
                DslContext.settingsRoot,
                role
            )))
    }

    // Release Build Chain
    listOf(sonarScan, release).forEach { buildType(it) }

    buildType(
        CodeReview(
            CodeReview.Config(
                vcsRoot = DslContext.settingsRoot,
                type = ANSIBLE,
                sonarScanConfig = sonarScanConfig
            )
        )
    )

}

class MoleculeTest(config: Config, buildConfig: BuildType.() -> Unit = {}) : BuildType({
    id("MoleculeTest_${config.role}")
    name = "Molecule Test ${config.role}"
    description = "Run molecule tests for ${config.role}."

    vcs {
        root(config.vcsRoot)
    }

    steps {
        script {
            scriptContent = """
                whoami
                cd roles/${config.role.lowercase()}; molecule test
            """.trimIndent()
        }
    }

    triggers {
        vcs {
            branchFilter = "+:<default>"
            quietPeriodMode = VcsTrigger.QuietPeriodMode.USE_DEFAULT
            triggerRules = "+:roles/${config.role}/**"
        }
    }

    apply(buildConfig)

}) {
    /**
     * Config class for [MoleculeTest] build configuration
     *
     * @param vcsRoot              The VCS root
     * @param role                 The role to be tested by this build configurations
     */
    data class Config(
        val vcsRoot: VcsRoot,
        val role: String
    )
}
