group = "no.elhub.${elhub_platform_name}
description = TODO("not implemented")

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}
