/*
 * Copyright 2017 ThoughtWorks, Inc.
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

package com.thoughtworks.go.config.update;

import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.CruiseConfig;
import com.thoughtworks.go.config.PipelineTemplateConfig;
import com.thoughtworks.go.config.TemplatesConfig;
import com.thoughtworks.go.config.exceptions.GoConfigInvalidException;
import com.thoughtworks.go.i18n.LocalizedMessage;
import com.thoughtworks.go.server.domain.Username;
import com.thoughtworks.go.server.service.SecurityService;
import com.thoughtworks.go.server.service.result.LocalizedOperationResult;
import com.thoughtworks.go.serverhealth.HealthStateType;

import java.util.List;

public class DeleteTemplateConfigCommand extends TemplateConfigCommand {

    private SecurityService securityService;

    public DeleteTemplateConfigCommand(PipelineTemplateConfig templateConfig, LocalizedOperationResult result, SecurityService securityService, Username currentUser) {
        super(templateConfig, result, currentUser);
        this.securityService = securityService;
    }

    @Override
    public void update(CruiseConfig modifiedConfig) throws Exception {
        preprocessedTemplateConfig = findAddedTemplate(modifiedConfig);
        TemplatesConfig templatesConfig = modifiedConfig.getTemplates();
        templatesConfig.removeTemplateNamed(preprocessedTemplateConfig.name());
        modifiedConfig.setTemplates(templatesConfig);
    }

    @Override
    public boolean isValid(CruiseConfig preprocessedConfig) {
        List<CaseInsensitiveString> pipelinesAssociatedWithTemplate = preprocessedConfig.pipelinesAssociatedWithTemplate(templateConfig.name());
        if (!pipelinesAssociatedWithTemplate.isEmpty()) {
            result.unprocessableEntity(LocalizedMessage.string("CANNOT_DELETE_RESOURCE_REFERENCED_BY_PIPELINES", "template", templateConfig.name(), pipelinesAssociatedWithTemplate));
            throw new GoConfigInvalidException(preprocessedConfig, String.format("The template '%s' is being referenced by pipeline(s): %s", templateConfig.name(), pipelinesAssociatedWithTemplate));
        }
        return true;
    }

    @Override
    public boolean canContinue(CruiseConfig cruiseConfig) {
        return doesTemplateExist(cruiseConfig) && isUserAuthorized();
    }

    private boolean isUserAuthorized() {
        if (!securityService.isAuthorizedToEditTemplate(templateConfig.name(), currentUser)) {
            result.unauthorized(LocalizedMessage.string("UNAUTHORIZED_TO_EDIT"), HealthStateType.unauthorised());
            return false;
        }
        return true;
    }

    private boolean doesTemplateExist(CruiseConfig cruiseConfig) {
        return cruiseConfig.findTemplate(templateConfig.name()) != null;
    }
}
