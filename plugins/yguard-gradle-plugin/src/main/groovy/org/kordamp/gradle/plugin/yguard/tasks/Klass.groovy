/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2019-2021 Andres Almiray.
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
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

import javax.inject.Inject

/**
 *
 * @author Andres Almiray
 * @since 0.1.0
 */
@CompileStatic
class Klass {
    final Property<String> name
    final Property<String> classes
    final Property<String> methods
    final Property<String> fields
    final Property<String> extends_
    final Property<String> implements_
    final ListProperty<String> includes
    final ListProperty<String> excludes

    @Inject
    Klass(Project project) {
        name = project.objects.property(String)
        classes = project.objects.property(String)
        methods = project.objects.property(String)
        fields = project.objects.property(String)
        extends_ = project.objects.property(String)
        implements_ = project.objects.property(String)
        includes = project.objects.listProperty(String)
        excludes = project.objects.listProperty(String)
    }

    List<String> validate() {
        List<String> errors = []

        if (classes.present) {
            if (!(classes.get() =~ /none|public|protected|friendly|private/)) {
                errors.add("\tInvalid value for keep.classes ('${classes.get()}'). Must be one of none, public, protected, friendly, private.".toString())
            }
        }
        if (methods.present) {
            if (!(methods.get() =~ /none|public|protected|friendly|private/)) {
                errors.add("\tInvalid value for keep.methods ('${methods.get()}'). Must be one of none, public, protected, friendly, private.".toString())
            }
        }
        if (fields.present) {
            if (!(fields.get() =~ /none|public|protected|friendly|private/)) {
                errors.add("\tInvalid value for keep.fields ('${fields.get()}'). Must be one of none, public, protected, friendly, private.".toString())
            }
        }

        errors
    }

    void include(String s) {
        includes.add(s)
    }

    void exclude(String s) {
        excludes.add(s)
    }

    boolean hasPatternSet() {
        (includes.present && includes.get().size() > 0) ||
            (excludes.present && excludes.get().size() > 0)
    }

    boolean isEmpty() {
        !name.present &&
            !classes.present &&
            !methods.present &&
            !fields.present &&
            !extends_.present &&
            !implements_.present &&
            !hasPatternSet()
    }
}