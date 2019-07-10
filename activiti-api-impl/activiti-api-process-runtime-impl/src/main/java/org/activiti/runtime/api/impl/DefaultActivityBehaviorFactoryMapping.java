/* Licensed under the Apache License, Version 2.0 (the "License");
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
package org.activiti.runtime.api.impl;

import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.impl.bpmn.behavior.CallActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.activiti.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.spring.process.ProcessExtensionService;
import org.activiti.spring.process.ProcessVariablesInitiator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Default implementation of the {@link ActivityBehaviorFactory}. Used when no custom {@link ActivityBehaviorFactory} is injected on the {@link ProcessEngineConfigurationImpl}.
 */
public class DefaultActivityBehaviorFactoryMapping extends DefaultActivityBehaviorFactory {

    private ProcessExtensionService processExtensionService;

    private ProcessVariablesInitiator processVariablesInitiator;

    public DefaultActivityBehaviorFactoryMapping(ProcessExtensionService processExtensionService,
                                                 ProcessVariablesInitiator processVariablesInitiator) {
        super();
        this.processExtensionService = processExtensionService;
        this.processVariablesInitiator = processVariablesInitiator;
    }

    @Override
    public UserTaskActivityBehavior createUserTaskActivityBehavior(UserTask userTask) {
        return new DefaultUserTaskBehavior(userTask,
                                           new VariablesMappingProvider(processExtensionService,
                                                                        processVariablesInitiator));
    }

    @Override
    public CallActivityBehavior createCallActivityBehavior(CallActivity callActivity) {

        String expressionRegex = "\\$+\\{+.+\\}";
        CallActivityBehavior callActivityBehaviour = null;
        if (StringUtils.isNotEmpty(callActivity.getCalledElement()) && callActivity.getCalledElement().matches(expressionRegex)) {
            callActivityBehaviour = new DefaultCallActivityBehavior(expressionManager.createExpression(callActivity.getCalledElement()),
                                                                    callActivity.getMapExceptions(),
                                                                    new VariablesMappingProvider(processExtensionService,
                                                                                                 processVariablesInitiator));
        } else {
            callActivityBehaviour = new DefaultCallActivityBehavior(callActivity.getCalledElement(),
                                                                    callActivity.getMapExceptions(),
                                                                    new VariablesMappingProvider(processExtensionService,
                                                                                                 processVariablesInitiator));
        }
        return callActivityBehaviour;
    }
}