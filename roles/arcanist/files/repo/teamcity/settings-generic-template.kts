import no.elhub.devxp.build.configuration.pipeline.ElhubProject.Companion.elhubProject
import no.elhub.devxp.build.configuration.pipeline.constants.Group
import no.elhub.devxp.build.configuration.pipeline.jobs.ansibleSonarScan
import no.elhub.devxp.build.configuration.pipeline.jobs.genericAutoRelease


elhubProject(Group.TEST, "test-project-name") {

    phabricator {
        ansibleSonarScan()
    }

    pipeline {
        sequential {
            ansibleSonarScan()
            genericAutoRelease()
        }
    }
}
