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
import org.gradle.api.Project
import org.gradle.api.provider.Property

import javax.inject.Inject

/**
 *
 * @author Andres Almiray
 * @since 0.1.0
 */
@CompileStatic
class Shrink {
    final Property<String> entryPointJar
    final Property<String> logFile
    final Property<Boolean> createStubs
    final Property<Boolean> enabled

    final Props props
    final Keep keep

    @Inject
    Shrink(Project project) {
        entryPointJar = project.objects.property(String)
        logFile = project.objects.property(String).convention('shrinklog.xml')
        createStubs = project.objects.property(Boolean).convention(false)
        enabled = project.objects.property(Boolean).convention(false)

        props = project.objects.newInstance(Props, project)
        keep = project.objects.newInstance(Keep, project)
    }

    void props(Action<? extends Props> action) {
        action.execute(props)
    }

    void keep(Action<? extends Keep> action) {
        action.execute(keep)
    }

    List<String> validate() {
        List<String> errors = []
        errors.addAll(props.validate())
        errors.addAll(keep.validate())
        errors
    }
}