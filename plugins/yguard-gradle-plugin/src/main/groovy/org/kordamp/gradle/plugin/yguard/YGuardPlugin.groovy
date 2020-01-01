/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2019-2020 Andres Almiray.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kordamp.gradle.plugin.yguard

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.AppliedPlugin
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.kordamp.gradle.plugin.yguard.tasks.YGuardGradleTask

/**
 *
 * @author Andres Almiray
 * @since 0.1.0
 */
@CompileStatic
class YGuardPlugin implements Plugin<Project> {
    static final String YGUARD = 'yGuard'

    @Override
    void apply(Project project) {
        Banner.display(project)

        project.pluginManager.withPlugin('java-base', new Action<AppliedPlugin>() {
            @Override
            void execute(AppliedPlugin p) {
                YGuardExtension extension = project.extensions.create(YGUARD, YGuardExtension)
                Configuration configuration = project.configurations.maybeCreate(YGUARD)

                addDependenciesAfterEvaluate(project, extension)

                String taskName = 'yGuardMain'
                TaskProvider<Jar> jar = project.tasks.named('jar', Jar)

                project.tasks.register(taskName, YGuardGradleTask, new Action<YGuardGradleTask>() {
                    @Override
                    @CompileDynamic
                    void execute(YGuardGradleTask t) {
                        t.dependsOn(jar)
                        t.group = BasePlugin.BUILD_GROUP
                        t.description = 'Obfuscates the jar archive containing the main classes'
                        t.classpath.from(configuration)
                        t.externalClasses.from(
                            project.configurations.findByName('compileClasspath'),
                            project.configurations.findByName('runtimeClasspath'))
                        t.inputJars.from(project.files(jar.get().archiveFile.get()))
                        t.outputDirectory.set(project.layout.buildDirectory.dir('yguard/main').get())
                    }
                })

                jar.configure(new Action<Jar>() {
                    @Override
                    void execute(Jar t) {
                        t.finalizedBy(project.tasks.named(taskName))
                    }
                })
            }
        })
    }

    @CompileDynamic
    private void addDependenciesAfterEvaluate(Project project,
                                              YGuardExtension extension) {
        project.afterEvaluate {
            if (extension.includeDefaultRepositories) {
                project.repositories.jcenter()
            }

            project.dependencies {
                yGuard("com.yworks:yguard:${extension.toolVersion}")
            }
        }
    }
}
