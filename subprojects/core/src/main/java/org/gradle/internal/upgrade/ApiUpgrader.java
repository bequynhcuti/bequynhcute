/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.upgrade;

import org.gradle.internal.hash.Hasher;
import org.objectweb.asm.MethodVisitor;

import java.util.Collections;
import java.util.List;

public class ApiUpgrader {
    public static final ApiUpgrader NO_UPGRADES = new ApiUpgrader(Collections.emptyList());

    private final List<Replacement> replacements;

    public ApiUpgrader(List<Replacement> replacements) {
        this.replacements = replacements;
    }

    public boolean generateReplacementMethod(MethodVisitor methodVisitor, int opcode, String owner, String name, String desc, boolean itf) {
        for (int index = 0, len = replacements.size(); index < len; index++) {
            Replacement replacement = replacements.get(index);
            if (replacement.replaceByteCodeIfMatches(opcode, owner, name, desc, itf, index, methodVisitor)) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldDecorateCallsiteArray() {
        return !replacements.isEmpty();
    }

    public void applyConfigurationTo(Hasher hasher) {
        for (Replacement replacement : replacements) {
            replacement.applyConfigurationTo(hasher);
        }
    }
}