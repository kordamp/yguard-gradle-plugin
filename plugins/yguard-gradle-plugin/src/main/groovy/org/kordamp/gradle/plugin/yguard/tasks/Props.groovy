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
package org.kordamp.gradle.plugin.yguard.tasks

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.provider.Property

import javax.inject.Inject

/**
 *
 * @author Andres Almiray
 * @since 0.1.0
 */
@CompileStatic
class Props {
    final Property<Boolean> errorChecking
    final Property<String> namingScheme
    final Property<String> languageConformity
    final Property<Boolean> overloadEnabled
    final Property<String> obfuscationPrefix
    final Property<String> digests
    final Property<String> exposeAttributes

    @Inject
    Props(Project project) {
        errorChecking = project.objects.property(Boolean).convention(false)
        namingScheme = project.objects.property(String)
        languageConformity = project.objects.property(String)
        overloadEnabled = project.objects.property(Boolean).convention(false)
        obfuscationPrefix = project.objects.property(String)
        digests = project.objects.property(String)
        exposeAttributes = project.objects.property(String)
    }

    final List<String> validate() {
        List<String> errors = []

        if (namingScheme.present) {
            if (!(namingScheme.get() =~ /small|best|mix/)) {
                errors.add("\tInvalid value for properties.namingScheme ('${namingScheme.get()}'). Must be one of small, best, mix.".toString())
            }
        }
        if (languageConformity.present) {
            if (!(languageConformity.get() =~ /compatible|legal|illegal/)) {
                errors.add("\tInvalid value for properties.languageConformity ('${languageConformity.get()}'). Must be one of compatible, legal, illegal.".toString())
            }
        }

        errors
    }
}