import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.SshAgent
import jetbrains.buildServer.configs.kotlin.v2019_2.project
import jetbrains.buildServer.configs.kotlin.v2019_2.sequential
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.version
import no.elhub.common.build.configuration.Assemble
import no.elhub.common.build.configuration.AutoRelease
import no.elhub.common.build.configuration.CodeReview
import no.elhub.common.build.configuration.ProjectType
import no.elhub.common.build.configuration.SonarScan
import no.elhub.common.build.configuration.UnitTest

version = "2022.04"

project {

    params {
        param("teamcity.ui.settings.readOnly", "true")
    }

    val projectId = "no.elhub.${elhub_platform_name}:${elhub_module_name}"
    val projectType = ProjectType.MAVEN
    val artifactoryRepository = "elhub-mvn-release-local"

    val sonarScanConfig = SonarScan.Config(
        vcsRoot = DslContext.settingsRoot,
        type = projectType,
        sonarId = projectId
    )

    buildType(
        CodeReview(
            CodeReview.Config(
                vcsRoot = DslContext.settingsRoot,
                type = projectType,
                sonarScanConfig = sonarScanConfig,
            )
        )
    )

    val unitTest = UnitTest(
        UnitTest.Config(
            vcsRoot = DslContext.settingsRoot,
            type = projectType
        )
    )

    val sonarScan = SonarScan(sonarScanConfig) {
        dependencies {
            snapshot(unitTest) { }
        }
    }

    val assemble = Assemble(
        Assemble.Config(
            vcsRoot = DslContext.settingsRoot,
            type = projectType
        )
    ) {
        dependencies {
            snapshot(sonarScan) { }
        }
    }

    val autoRelease = AutoRelease(
        AutoRelease.Config(
            vcsRoot = DslContext.settingsRoot,
            type = projectType,
        )
    ) {
        dependencies {
            snapshot(assemble) { }
        }
    }

    val publishDocs = PublishDocs(
        PublishDocs.Config(
            vcsRoot = DslContext.settingsRoot,
            type = projectType,
            dest = "no.elhub.${elhub_platform_name}/${elhub_module_name}"
        )
    ) {
        dependencies {
            snapshot(autoRelease) { }
        }
    }

    listOf(unitTest, sonarScan, assemble, autoRelease, publishDocs).forEach { buildType(it) }
}
