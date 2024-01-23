//plugins {
//    // Can't use libs.versions.toml here
//    id("org.jlleitschuh.gradle.ktlint")
//}
//
//repositories {
//    mavenCentral()
//}
//
//dependencies {
//    ktlintRuleset(libs.ktlint.ruleset.compose)
//}
//
//ktlint {
//    verbose.set(true)
//    outputToConsole.set(true)
//    android.set(true)
//    // add editorconfig row: "ktlint_function_naming_ignore_when_annotated_with=Composable"
//    additionalEditorconfig.set(
//        mapOf(
//            "ktlint_function_naming_ignore_when_annotated_with" to "Composable"
//        )
//    )
//    filter {
//        exclude("**/generated/**")
//    }
//
//    reporters {
//        reporter(ReporterType.CHECKSTYLE)
//        reporter(ReporterType.JSON)
//    }
//}