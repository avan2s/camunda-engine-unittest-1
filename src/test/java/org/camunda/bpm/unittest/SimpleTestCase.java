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
package org.camunda.bpm.unittest;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.processEngine;

/**
 * @author avan2s
 */
public class SimpleTestCase {

    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule();

    @Test
    public void shouldExecuteProcess() throws IOException {
        VariableMap variables = Variables.createVariables().putValue("gender", "female");
        String decisionDefinitionKey = "dmnColor";
        String decisionDefinitionName = "colorDefinitionName";

        // Read the file
        InputStream inputStream = new FileInputStream("dmnColor.dmn");
        //this.getClass().getResourceAsStream("dmn/dmnColor.dmn");

        // Getting the content of the file in plain-text org.apache.commons.io.IOUtils
        String clearTextContent = IOUtils.toString(inputStream, Charset.defaultCharset().name());
        inputStream.close();

        inputStream = new ByteArrayInputStream(clearTextContent.getBytes());
        DmnModelInstance dmnModelInstance = Dmn.readModelFromStream(inputStream);
        // Validiate the model - works fine
        Dmn.validateModel(dmnModelInstance);

        // Add .dmn to the file, if it not exists
        String resourceName = decisionDefinitionName.endsWith(".dmn") ? decisionDefinitionName : decisionDefinitionName + ".dmn";
        // deploy the resource
        processEngine().getRepositoryService().createDeployment().name(decisionDefinitionName).addString(resourceName, clearTextContent).deploy();

        DmnDecisionTableResult colorDecisionResult = processEngine().getDecisionService().evaluateDecisionTableByKey(decisionDefinitionKey, variables);
    }

}
