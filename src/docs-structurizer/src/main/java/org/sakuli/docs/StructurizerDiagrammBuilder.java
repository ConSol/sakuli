/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
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

/**
 * @author tschneck
 * Date: 1/30/16
 */
package org.sakuli.docs;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.componentfinder.ComponentFinder;
import com.structurizr.componentfinder.NamePrefixComponentFinderStrategy;
import com.structurizr.componentfinder.NameSuffixComponentFinderStrategy;
import com.structurizr.componentfinder.SpringComponentFinderStrategy;
import com.structurizr.model.*;
import com.structurizr.view.*;

public class StructurizerDiagrammBuilder {
    public static void main(String[] args) throws Exception {
        Workspace workspace = new Workspace("Sakuli E2E testing and monitoring",
                "This is a C4 representation of the Sakuli software");
        Model model = workspace.getModel();


        //systems and persons
        SoftwareSystem sakuliSystem = model.addSoftwareSystem("Sakuli",
                "Allows devolpers and tester to monitor or test a software from the end-2-end point of view");
        sakuliSystem.setLocation(Location.Internal);
        SoftwareSystem systemUnderTest = model.addSoftwareSystem("SUT", "System under Test");
        systemUnderTest.setLocation(Location.External);
        systemUnderTest.addTags("SUT");
        SoftwareSystem monitioringSystem = model.addSoftwareSystem("Nagios Monitoring", "A nagios based monitoring system");
        monitioringSystem.setLocation(Location.External);
        SoftwareSystem database = model.addSoftwareSystem("MySQL Database", "A MySQL database to store the Sakuli results");
        database.setLocation(Location.External);
        Person tester = model.addPerson("Tester", "A software tester");
        Person operator = model.addPerson("Operator", "A software operator");

        tester.uses(sakuliSystem, "uses");
        operator.uses(monitioringSystem, "uses");
        sakuliSystem.uses(systemUnderTest, "tests", "HTTP / HTTPS, UI interaction", InteractionStyle.Synchronous);
        sakuliSystem.uses(monitioringSystem, "reports", "HTTP (Gearman protocoll)", InteractionStyle.Asynchronous);
        sakuliSystem.uses(database, "reports", "JDBC", InteractionStyle.Synchronous);
        monitioringSystem.uses(database, "uses", "JDBC", InteractionStyle.Asynchronous);
        sakuliSystem.delivers(tester, "Testing Results");
        monitioringSystem.delivers(operator, "Monitoring Repors");

        ViewSet viewSet = workspace.getViews();
        SystemContextView contextView = viewSet.createContextView(sakuliSystem);
        contextView.setKey("context");
        contextView.addAllSoftwareSystems();
        contextView.addAllPeople();
        contextView.addAllElements();


        //javascripte starter
        Container jsStarter = sakuliSystem.addContainer("JavaScript Starter", "Starter for JavaScript Testcases",
                ".sh / .bat script and Java binaries");

        tester.uses(jsStarter, "uses");

        new ComponentFinder(
                jsStarter, "org.sakuli.starter",
                new SpringComponentFinderStrategy(),
                new NameSuffixComponentFinderStrategy("Starter")
        ).findComponents();
        jsStarter.getComponents().stream()
                .filter(c -> c.getTechnology().equals(SpringComponentFinderStrategy.SPRING_COMPONENT))
                .forEach(c -> c.addTags("Spring Component"));
        jsStarter.uses(sakuliSystem, "contains");
        jsStarter.uses(jsStarter.getComponentWithName("SakuliStarter"), "calls");
        ComponentView jsStarterView = viewSet.createComponentView(jsStarter);
        jsStarterView.setKey("starter-components");
        jsStarterView.add(tester);
        jsStarterView.add(jsStarter);
        jsStarterView.addAllComponents();

        //java Starter
        Container javastarter = sakuliSystem.addContainer("Java Starter", "Starter for Java Testcases",
                "TestNG Test");
        tester.uses(javastarter, "uses");
        new ComponentFinder(
                javastarter, "org.sakuli.javaDSL",
                new SpringComponentFinderStrategy(),
                new NamePrefixComponentFinderStrategy("AbstractSakuliTest"),
                new NameSuffixComponentFinderStrategy("Service")
        ).findComponents();
        javastarter.getComponents().stream()
                .filter(c -> c.getTechnology().equals(SpringComponentFinderStrategy.SPRING_COMPONENT))
                .forEach(c -> c.addTags("Spring Component"));
        javastarter.uses(sakuliSystem, "contains");
        javastarter.uses(javastarter.getComponentWithName("InitializingService"), "calls");
        ComponentView javaStarterView = viewSet.createComponentView(javastarter);
        javaStarterView.add(javastarter);
        javaStarterView.add(tester);
        javaStarterView.addAllComponents();

        //actions
        Container actions = sakuliSystem.addContainer("Sakuli Actions", "Execution the HTML/UI Actions", "Java Library");
        actions.uses(systemUnderTest, "controls");
        Component sahi = actions.addComponent("Sahi Library", "Sahi HTML control functions");
        sahi.uses(systemUnderTest, "controls");
        sahi.addTags("LIB");
        Component sikuli = actions.addComponent("SikuliX Library", "SikuliX UI control functions");
        sikuli.uses(systemUnderTest, "controls");
        sikuli.addTags("LIB");
        ComponentFinder componentFinder = new ComponentFinder(
                actions, "org.sakuli.actions",
                new SpringComponentFinderStrategy()
                , new NamePrefixComponentFinderStrategy("Appli")
                , new NamePrefixComponentFinderStrategy("Region")
                , new NamePrefixComponentFinderStrategy("Environment")
                , new NameSuffixComponentFinderStrategy("Action")
        );
        componentFinder.findComponents();
        componentFinder.enrichComponent("Component", "Class", null, null, null);
        actions.getComponentWithName("RegionImpl").uses(sikuli, "calls");
        actions.getComponentWithName("Environment").uses(sikuli, "calls");
        //actions.getComponentWithName("Application").uses(sikuli, "calls");

        actions.getComponentWithName("Action").uses(sahi, "calls");
        actions.getComponentWithName("Action").uses(actions.getComponentWithName("TestCaseAction"), "");
        actions.uses(actions.getComponentWithName("Action"), "calls");
        actions.getComponents().stream()
                .filter(c -> c.getTechnology().equals(SpringComponentFinderStrategy.SPRING_COMPONENT))
                .forEach(c -> c.addTags("Spring Component"));
        ComponentView actionView = viewSet.createComponentView(actions);
        actionView.add(actions);
        actionView.add(systemUnderTest);
        actionView.addAllComponents();

        //containers view
        ContainerView containerView = viewSet.createContainerView(sakuliSystem);
        containerView.setKey("containers");
        containerView.addAllPeople();
        containerView.addAllSoftwareSystems();
        containerView.addAllContainers();

        // link the architecture model with the code
        jsStarter.getComponents().stream().forEach(c -> c.setSourcePath("https://github.com/ConSol/sakuli/tree/master/src/core"));
        actions.getComponents().stream().forEach(c -> c.setSourcePath("https://github.com/ConSol/sakuli/tree/master/src/core"));
        javastarter.getComponents().stream().forEach(c -> c.setSourcePath("https://github.com/ConSol/sakuli/tree/master/src/java-dsl"));


        Styles styles = viewSet.getConfiguration().getStyles();
        styles.addElementStyle(Tags.PERSON).setShape(Shape.Person);
        styles.addElementStyle(Tags.PERSON).background("#FF9673");
        styles.addElementStyle("Spring Component").background("#91D366");
        styles.addElementStyle("SUT").background("#FAFA55");
        styles.addElementStyle(Tags.ELEMENT).color("#147FA3");
        styles.addElementStyle("LIB").color("#96781B");

        StructurizrClient structurizrClient = new StructurizrClient("d6d9e2c4-c866-4a51-b70c-064b43c4e045", "e2dd9013-9515-4b94-8732-c2fa0d4e9282");
        structurizrClient.mergeWorkspace(8341, workspace);
    }
}
