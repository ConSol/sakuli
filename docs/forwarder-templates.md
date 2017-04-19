# Using Jtwig templates in Sakuli

With release 1.1 Sakuli introduced the [Jtwig](http://jtwig.org/) template engine, which is used for generating the output of the Sakuli test suite for certain forwarders. (Currently only the [Check_MK forwarder](forwarder-checkmk.md) supports Jtwig templates.)

Using templates for the forwarder output makes it possible to easily modify the output of  Sakuli and adapt it without the need to release a new Sakuli version.

Here we want to describe how Jtwig is working, how to use a customized template and what you have to pay attention to.

## JTwig template
[Jtwig] is a template engine based on Java, which has a simple and understandable syntax und is easy to use. The following snippet shows an example of a Jtwig template used in Sakuli.


```.xml
    {% for testStep in testCase.stepsAsSortedSet %}
        , step "{{ testStep.name }}"$whitespace$
        {% if (testStep.state.error) %}
            EXCEPTION: {{ errorMessageCreator.exceptionMessage(testStep) }}
        {% else %}
            over runtime ({{ testStep.duration }}s/warn at {{ testStep.warningTime }}s)
        {% endif %}
    {% endfor %}
```

The input for this template is the ``testCase`` object, which has a set ot test steps. The template iterates over all the steps and prints certain step information based on the state of the test step.

The whole [Jtwig] feature set is supported and can be used within the Sakuli forwarder.
For more detailed information about Jtwig templates, please refer to [Jtwig].


## Customized Jtwig Functions

[Jtwig] comes with a list of functions like `abs`, `concat` etc, which can be used within a template. In addition to the standard functions, Jtwig supports the implementation and injection of custom functions. Sakuli provides following custom functions:
* [`abbreviate`](../src/sakuli-core/src/main/java/org/sakuli/services/forwarder/configuration/AbbreviateFunction.java) - The function abbreviates a certain string to a specified length. The implementation is based on the StringUtils.abbreviate method from the Apache commons library.
* [`extractScreenshot`](../src/sakuli-core/src/main/java/org/sakuli/services/forwarder/configuration/ExtractScreenshotFunction.java) - The function extracts the screenshot from a specified test data entity, which contains an exception.
* [`getOutputDuration`](../src/sakuli-core/src/main/java/org/sakuli/services/forwarder/configuration/GetOutputDurationFunction.java) - The function returns the formatted duration for a specified test entity.
* [`getOutputState`](../src/sakuli-core/src/main/java/org/sakuli/services/forwarder/configuration/GetOutputStateFunction.java) - The function returns the [OutputState](../src/sakuli-core/src/main/java/org/sakuli/datamodel/state/SakuliState.java) for a specified SakuliState.
* [`isBlank`](../src/sakuli-core/src/main/java/org/sakuli/services/forwarder/configuration/IsBlankFunction.java) - The function checks whether the specified string parameter is blank.

Please refer to the Sakuli [default Jtwig templates](../src/sakuli-common/src/main/resources/org/sakuli/common/config/templates), to see the custom functions in action.


## Handling of white spaces and new lines
The forwarder implementation provides a special handling of white spaces and new lines within the templates. For this feature the `jtwig-spaceless-extension` has been used, which comes with a SpaceRemover interface. This has been implemented by the `LeadingWhitespaceRemover`. The `LeadingWhitespaceRemover` is configured in the twig template engine and removes all leading whitespaces for every line and all line endings within the template. It can be activated by adding the text snippet or the whole template within the following tags `{% spaceless %}` and `{% endspaceless %}`.
The `LeadingWhitespaceRemover` has been introduced, to make the implementation and maintaining of the templates easier.

Since the leading white spaces and the new lines are removed, some special placeholder have been introduced, which are replaced by the real characters at the end of the template generation:
* `$whitespace$` - the placeholder will be replaced by a real white space.
* `$newline$` - the placeholder will be replaced by a new line `'\n'`.


## Using a customized templates

Sakuli provides a default template set, which is placed within the templates directory under `__SAKULI_HOME__/config/templates` within the Sakuli home folder (e.g. the default Check_MK templates can be found in a subdirectory `check_mk`).

The property `sakuli.forwarder.template.folder` defines the path to the main.twig template for the respective forwarder. The default value is set to `${sakuli.home.folder}/config/templates`.

Before customizing a default template, it is essential to copy the templates to a new location and configure that location as the template location in Sakuli, otherwise installing a new Sakuli release would overwrite the modified templates within the config directory.

## Structure of the default main template
Generally the forwarder is expecting to find a certain template file named `main.twig` within the configured template directory. The default `main.twig` template doesn't contain the whole output, but includes further templates to make the single templates more readable. Since the templates are fully customizable, the user can decide how the templates are structured, whether to use nested templates or to define everything within a single file.

The following snippet shows the default `main.twig` template provided by Sakuli for the Check_MK forwarder. This template is also showing the use of the spaceless extension and the special character for a new line.


```.xml
    {% spaceless %}
    {% import 'error_message_creator.twig' as errorMessageCreator %}
        <<<local>>>$newline$
        {% include 'performance_data.twig' %}
        {% include 'short_summary.twig' %}
        {% include 'detailed_summary.twig' %}
        $newline$
    {% endspaceless %}
```

## Supported Forwarder
* [Check_MK](forwarder-checkmk.md)

[Jtwig]: http://jtwig.org/
