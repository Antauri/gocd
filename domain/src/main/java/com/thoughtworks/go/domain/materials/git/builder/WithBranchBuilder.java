/*
 * Copyright 2019 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thoughtworks.go.domain.materials.git.builder;

import com.thoughtworks.go.util.command.CommandLine;
import com.thoughtworks.go.util.command.SecretString;

import java.io.File;
import java.util.List;

public class WithBranchBuilder implements Builder {
    private final GitLogCommandBuilder mainBuilder;
    private CommandLine git;

    WithBranchBuilder(GitLogCommandBuilder mainBuilder, CommandLine git) {
        this.mainBuilder = mainBuilder;
        this.git = git;
    }

    public Builder withRemoteBranch(String remoteBranch) {
        git.withArg(remoteBranch);
        return mainBuilder;
    }

    @Override
    public Builder withWorkingDir(File workingDir) {
        return mainBuilder.withWorkingDir(workingDir);
    }

    @Override
    public Builder outputFormatYaml() {
        return mainBuilder.outputFormatYaml();
    }

    @Override
    public Builder withNonArgSecrets(List<SecretString> secrets) {
        return mainBuilder.withNonArgSecrets(secrets);
    }

    @Override
    public CommandLine build() {
        return mainBuilder.build();
    }
}
