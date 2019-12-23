/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2019 Andres Almiray.
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
package org.kordamp.gradle.plugin.yguard.tasks

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 *
 * @author Andres Almiray
 * @since 0.1.0
 */
@CompileStatic
class YGuardGradleTask extends DefaultTask {
    @Classpath
    final FileCollection classpath = project.objects.fileCollection()

    @Classpath
    final FileCollection externalClasses = project.objects.fileCollection()

    @Classpath
    final FileCollection excludeExternalClasses = project.objects.fileCollection()

    @InputFiles
    final FileCollection inputJars = project.objects.fileCollection()

    @Input
    final Property<Boolean> verbose = project.objects.property(Boolean).convention(false)

    @OutputDirectory
    final DirectoryProperty outputDirectory = project.objects.directoryProperty().convention(
        project.layout.buildDirectory.dir('yguard')
    )

    @Input
    @Optional
    final Property<String> resources = project.objects.property(String).convention('copy')

    @Internal
    final Attribute attribute = project.objects.newInstance(Attribute, project)
    @Internal
    final Shrink shrink = project.objects.newInstance(Shrink, project)
    @Internal
    final Rename rename = project.objects.newInstance(Rename, project)

    @TaskAction
    void executeYguard() {
        List<String> errors = []

        if(!shrink.enabled.get() && !rename.enabled.get()) {
            throw new IllegalStateException("Task ${path} is misconfigured, shrink and rename cannot be disabled at the same time.")
        }

        if (resources.present) {
            if (!(resources.get() =~ /copy|auto|none/)) {
                errors.add("\tInvalid value for resources ('${resources.get()}'). Must be one of none, copy, auto.".toString())
            }
        }

        List<String> e = attribute.validate()
        if (e) {
            errors.add('attribute:')
            errors.addAll(e)
        }

        e = shrink.validate()
        if (e) {
            errors.add('shrink:')
            errors.addAll(e)
        }

        e = rename.validate()
        if (e) {
            errors.add('rename:')
            errors.addAll(e)
        }

        if (errors) {
            throw new IllegalStateException("Task ${path} is not properly configured.\n${errors.join('\n')}")
        }

        YGuardTaskInvoker.invoke(this)
    }

    void attribute(Action<? extends Attribute> action) {
        action.execute(attribute)
    }

    void shrink(Action<? extends Shrink> action) {
        action.execute(shrink)
    }

    void rename(Action<? extends Rename> action) {
        action.execute(rename)
    }
}
