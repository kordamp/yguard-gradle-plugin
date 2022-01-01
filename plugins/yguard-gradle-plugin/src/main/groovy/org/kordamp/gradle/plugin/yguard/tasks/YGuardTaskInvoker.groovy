/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2019-2022 Andres Almiray.
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

import org.gradle.api.file.FileCollection

/**
 *
 * @author Andres Almiray
 * @since 0.1.0
 */
class YGuardTaskInvoker {
    static void invoke(YGuardGradleTask task) {
        if (task.resolvedVerbose.get()) {
            task.ant.setLifecycleLogLevel('INFO')
        }

        FileCollection external = task.project.objects.fileCollection()
        external.from(task.externalClasses)
        external = external.minus(task.excludeExternalClasses)

        task.ant.taskdef(name: 'yguard',
            classname: 'com.yworks.yguard.YGuardTask',
            classpath: task.classpath.asPath)

        task.ant.yguard {
            for (File file : task.inputJars.files) {
                inOutPair(
                    in: file,
                    out: task.outputDirectory.get().file(file.name[0..-5] + '_obf.jar').asFile,
                    resources: task.resolvedResources.get()
                )
            }

            if (task.attribute.name.present) {
                attribute(name: task.attribute.name.get()) {
                    if (task.attribute.hasPatternSet()) {
                        patternSet {
                            task.attribute.includes.each { elem ->
                                include(name: elem)
                            }
                            task.attribute.excludes.each { elem ->
                                exclude(name: elem)
                            }
                        }
                    }
                }
            }

            if (external.size()) {
                externalClasses {
                    for (File file : external.files) {
                        pathElement(location: file.absolutePath)
                    }
                }
            }

            if (task.shrink.enabled.get()) {
                shrink(
                    logFile: task.outputDirectory.get().file(task.shrink.logFile.get()).asFile,
                    createStubs: task.shrink.createStubs.get()) {
                    if (task.shrink.entryPointJar.present) {
                        entryPointJar(name: task.shrink.entryPointJar.get())
                    }
                    if (task.shrink.props.errorChecking.get()) {
                        property(name: 'error-checking', value: 'pedantic')
                    }
                    if (task.shrink.props.namingScheme.present) {
                        property(name: 'naming-scheme', value: task.shrink.props.namingScheme.get())
                    }
                    if (task.shrink.props.languageConformity.present) {
                        property(name: 'language-conformity', value: task.shrink.props.languageConformity.get())
                    }
                    if (task.shrink.props.overloadEnabled.present) {
                        property(name: 'overload-enabled', value: task.shrink.props.overloadEnabled.get())
                    }
                    if (task.shrink.props.obfuscationPrefix.present) {
                        property(name: 'obfuscation-prefix', value: task.shrink.props.obfuscationPrefix.get())
                    }
                    if (task.shrink.props.digests.present) {
                        property(name: 'digests', value: task.shrink.props.digests.get())
                    }
                    if (task.shrink.props.exposeAttributes.present) {
                        property(name: 'expose-attributes', value: task.shrink.props.exposeAttributes.get())
                    }

                    keep(
                        sourcefile: task.shrink.keep.sourcefile.getOrElse(false),
                        localvariabletable: task.shrink.keep.sourcefile.getOrElse(false),
                        localVariableTypeTable: task.shrink.keep.sourcefile.getOrElse(false),
                        linenumbertable: task.shrink.keep.sourcefile.getOrElse(false),
                        runtimeVisibleAnnotations: task.shrink.keep.sourcefile.getOrElse(true),
                        runtimeVisibleTypeAnnotations: task.shrink.keep.sourcefile.getOrElse(true),
                        runtimeInvisibleAnnotations: task.shrink.keep.sourcefile.getOrElse(false),
                        runtimeInvisibleTypeAnnotations: task.shrink.keep.sourcefile.getOrElse(false),
                        runtimeVisibleParameterAnnotations: task.shrink.keep.sourcefile.getOrElse(true),
                        runtimeInvisibleParameterAnnotations: task.shrink.keep.sourcefile.getOrElse(false),
                        sourceDebugExtension: task.shrink.keep.sourcefile.getOrElse(false)) {
                        task.shrink.keep.classes.each { c ->
                            if (!c.empty) {
                                Map<String, ?> params = [:]
                                if (c.name.present) params.name = c.name.get()
                                if (c.classes.present) params.classes = c.classes.get()
                                if (c.methods.present) params.methods = c.methods.get()
                                if (c.fields.present) params.fields = c.fields.get()
                                if (c.extends_.present) params.extends = c.extends_.get()
                                if (c.implements_.present) params.implements = c.implements_.get()
                                'class'(params) {
                                    if (c.hasPatternSet()) {
                                        patternSet {
                                            c.includes.each { elem ->
                                                include(name: elem)
                                            }
                                            c.excludes.each { elem ->
                                                exclude(name: elem)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        task.shrink.keep.methods.each { m ->
                            if (!m.empty) {
                                Map<String, ?> params = [:]
                                if (m.name.present) params.name = m.name.get()
                                if (m.class_.present) params.'class' = m.class_.get()
                                method(params) {
                                    if (m.hasPatternSet()) {
                                        patternSet {
                                            m.includes.each { elem ->
                                                include(name: elem)
                                            }
                                            m.excludes.each { elem ->
                                                exclude(name: elem)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        task.shrink.keep.fields.each { f ->
                            if (!f.empty) {
                                Map<String, ?> params = [:]
                                if (f.name.present) params.name = f.name.get()
                                if (f.class_.present) params.'class' = f.class_.get()
                                field(params) {
                                    if (f.hasPatternSet()) {
                                        patternSet {
                                            f.includes.each { elem ->
                                                include(name: elem)
                                            }
                                            f.excludes.each { elem ->
                                                exclude(name: elem)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (task.rename.enabled.get()) {
                rename(
                    logFile: task.outputDirectory.get().file(task.rename.logFile.get()).asFile,
                    mainClass: task.rename.mainClass.orNull,
                    conserveManifest: task.rename.conserveManifest.get(),
                    replaceClassNameStrings: task.rename.replaceClassNameStrings.get(),
                    annotationClass: task.rename.annotationClass.get()) {
                    if (task.rename.props.errorChecking.get()) {
                        property(name: 'error-checking', value: 'pedantic')
                    }
                    if (task.rename.props.namingScheme.present) {
                        property(name: 'naming-scheme', value: task.rename.props.namingScheme.get())
                    }
                    if (task.rename.props.languageConformity.present) {
                        property(name: 'language-conformity', value: task.rename.props.languageConformity.get())
                    }
                    if (task.rename.props.overloadEnabled.present) {
                        property(name: 'overload-enabled', value: task.rename.props.overloadEnabled.get())
                    }
                    if (task.rename.props.obfuscationPrefix.present) {
                        property(name: 'obfuscation-prefix', value: task.rename.props.obfuscationPrefix.get())
                    }
                    if (task.rename.props.digests.present) {
                        property(name: 'digests', value: task.rename.props.digests.get())
                    }
                    if (task.rename.props.exposeAttributes.present) {
                        property(name: 'expose-attributes', value: task.rename.props.exposeAttributes.get())
                    }

                    keep(
                        sourcefile: task.rename.keep.sourcefile.getOrElse(false),
                        localvariabletable: task.rename.keep.sourcefile.getOrElse(false),
                        localVariableTypeTable: task.rename.keep.sourcefile.getOrElse(false),
                        linenumbertable: task.rename.keep.sourcefile.getOrElse(false),
                        runtimeVisibleAnnotations: task.rename.keep.sourcefile.getOrElse(true),
                        runtimeVisibleTypeAnnotations: task.rename.keep.sourcefile.getOrElse(true),
                        runtimeInvisibleAnnotations: task.rename.keep.sourcefile.getOrElse(true),
                        runtimeInvisibleTypeAnnotations: task.rename.keep.sourcefile.getOrElse(true),
                        runtimeVisibleParameterAnnotations: task.rename.keep.sourcefile.getOrElse(true),
                        runtimeInvisibleParameterAnnotations: task.rename.keep.sourcefile.getOrElse(true),
                        sourceDebugExtension: task.rename.keep.sourcefile.getOrElse(false)) {
                        task.rename.keep.classes.each { c ->
                            if (!c.empty) {
                                Map<String, ?> params = [:]
                                if (c.name.present) params.name = c.name.get()
                                if (c.classes.present) params.classes = c.classes.get()
                                if (c.methods.present) params.methods = c.methods.get()
                                if (c.fields.present) params.fields = c.fields.get()
                                if (c.extends_.present) params.extends = c.extends_.get()
                                if (c.implements_.present) params.implements = c.implements_.get()
                                'class'(params) {
                                    if (c.hasPatternSet()) {
                                        patternSet {
                                            c.includes.each { elem ->
                                                include(name: elem)
                                            }
                                            c.excludes.each { elem ->
                                                exclude(name: elem)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        task.rename.keep.methods.each { m ->
                            if (!m.empty) {
                                Map<String, ?> params = [:]
                                if (m.name.present) params.name = m.name.get()
                                if (m.class_.present) params.'class' = m.class_.get()
                                method(params) {
                                    if (m.hasPatternSet()) {
                                        patternSet {
                                            m.includes.each { elem ->
                                                include(name: elem)
                                            }
                                            m.excludes.each { elem ->
                                                exclude(name: elem)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        task.rename.keep.fields.each { f ->
                            if (!f.empty) {
                                Map<String, ?> params = [:]
                                if (f.name.present) params.name = f.name.get()
                                if (f.class_.present) params.'class' = f.class_.get()
                                field(params) {
                                    if (f.hasPatternSet()) {
                                        patternSet {
                                            f.includes.each { elem ->
                                                include(name: elem)
                                            }
                                            f.excludes.each { elem ->
                                                exclude(name: elem)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        task.rename.keep.packages.each { p ->
                            if (!p.empty) {
                                'package'() {
                                    patternSet {
                                        p.includes.each { elem ->
                                            include(name: elem)
                                        }
                                        p.excludes.each { elem ->
                                            exclude(name: elem)
                                        }
                                    }
                                }
                            }
                        }
                        task.rename.keep.sourceFiles.each { s ->
                            if (!s.empty) {
                                sourceFile {
                                    if (s.mapping.present) {
                                        property(name: 'mapping', value: s.mapping.get())
                                    }
                                    if (s.hasPatternSet()) {
                                        patternSet {
                                            s.includes.each { elem ->
                                                include(name: elem)
                                            }
                                            s.excludes.each { elem ->
                                                exclude(name: elem)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        task.rename.keep.lineNumberTables.each { l ->
                            if (!l.empty) {
                                lineNumberTable {
                                    if (l.mappingScheme.present) {
                                        property(name: 'mapping-scheme', value: l.mappingScheme.get())
                                    }
                                    if (l.scramblingSalt.present) {
                                        property(name: 'scrambling-salt', value: l.scramblingSalt.get())
                                    }
                                    if (l.hasPatternSet()) {
                                        patternSet {
                                            l.includes.each { elem ->
                                                include(name: elem)
                                            }
                                            l.excludes.each { elem ->
                                                exclude(name: elem)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    task.rename.adjusts.each { a ->
                        if (!a.empty) {
                            Map<String, ?> params = [:]
                            if (a.file.present) params.file = a.file.get()
                            params.replaceName = a.replaceName.getOrElse(false)
                            params.replaceContent = a.replaceContent.getOrElse(false)
                            params.replacePath = a.replacePath.getOrElse(true)
                            adjust(params) {
                                if (a.hasPatternSet()) {
                                    patternSet {
                                        a.includes.each { elem ->
                                            include(name: elem)
                                        }
                                        a.excludes.each { elem ->
                                            exclude(name: elem)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
