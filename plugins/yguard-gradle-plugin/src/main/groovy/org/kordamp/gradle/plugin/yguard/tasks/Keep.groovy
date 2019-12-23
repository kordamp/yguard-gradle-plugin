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

import groovy.transform.CompileDynamic
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
class Keep {
    final Property<Boolean> sourcefile
    final Property<Boolean> localvariabletable
    final Property<Boolean> localVariableTypeTable
    final Property<Boolean> linenumbertable
    final Property<Boolean> runtimeVisibleAnnotations
    final Property<Boolean> runtimeVisibleTypeAnnotations
    final Property<Boolean> runtimeInvisibleAnnotations
    final Property<Boolean> runtimeInvisibleTypeAnnotations
    final Property<Boolean> runtimeVisibleParameterAnnotations
    final Property<Boolean> runtimeInvisibleParameterAnnotations
    final Property<Boolean> sourceDebugExtension

    final List<Klass> classes = []
    final List<Method> methods = []
    final List<Field> fields = []
    final List<Package> packages = []
    final List<SourceFile> sourceFiles = []
    final List<LineNumberTable> lineNumberTables = []
    final List<Attribute> attributes = []

    private final Project project

    @Inject
    Keep(Project project) {
        this.project = project
        sourcefile = project.objects.property(Boolean).convention(false)
        localvariabletable = project.objects.property(Boolean).convention(false)
        localVariableTypeTable = project.objects.property(Boolean).convention(false)
        linenumbertable = project.objects.property(Boolean).convention(false)
        runtimeVisibleAnnotations = project.objects.property(Boolean).convention(true)
        runtimeVisibleTypeAnnotations = project.objects.property(Boolean).convention(true)
        runtimeInvisibleAnnotations = project.objects.property(Boolean).convention(false)
        runtimeInvisibleTypeAnnotations = project.objects.property(Boolean).convention(false)
        runtimeVisibleParameterAnnotations = project.objects.property(Boolean).convention(true)
        runtimeInvisibleParameterAnnotations = project.objects.property(Boolean).convention(false)
        sourceDebugExtension = project.objects.property(Boolean).convention(false)
    }

    void class_(Action<? extends Klass> action) {
        Klass klass = project.objects.newInstance(Klass, project)
        action.execute(klass)
        classes.add(klass)
    }

    void method(Action<? extends Method> action) {
        Method method = project.objects.newInstance(Method, project)
        action.execute(method)
        methods.add(method)
    }

    void field(Action<? extends Field> action) {
        Field field = project.objects.newInstance(Field, project)
        action.execute(field)
        fields.add(field)
    }

    void package_(Action<? extends Package> action) {
        Package p = project.objects.newInstance(Package, project)
        action.execute(p)
        packages.add(p)
    }

    void sourceFile(Action<? extends SourceFile> action) {
        SourceFile sourceFile = project.objects.newInstance(SourceFile, project)
        action.execute(sourceFile)
        sourceFiles.add(sourceFile)
    }

    void lineNumberTable(Action<? extends LineNumberTable> action) {
        LineNumberTable lineNumberTable = project.objects.newInstance(LineNumberTable, project)
        action.execute(lineNumberTable)
        lineNumberTables.add(lineNumberTable)
    }

    void attribute(Action<? extends Attribute> action) {
        Attribute attribute = project.objects.newInstance(Attribute, project)
        action.execute(attribute)
        attributes.add(attribute)
    }

    @CompileDynamic
    List<String> validate() {
        List<String> errors = []
        errors.addAll(classes*.validate().flatten())
        errors.addAll(methods*.validate().flatten())
        errors.addAll(fields*.validate().flatten())
        errors.addAll(packages*.validate().flatten())
        errors.addAll(sourceFiles*.validate().flatten())
        errors.addAll(lineNumberTables*.validate().flatten())
        errors.addAll(attributes*.validate().flatten())
        errors
    }
}