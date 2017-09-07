<?php
/**
This template is used for sakuli.
PHP version 5
@category Template_File
@package Histou/templates/default
@author Philip Griesbacher <griesbacher@consol.de>
@license http://opensource.org/licenses/gpl-license.php GNU Public License
@link https://github.com/Griesbacher/histou
**/

$rule = new \histou\template\Rule(
    $host = '.*',
    $service = '.*',
    $command = '.*',
    $perfLabel = array('^c_\d\d\d_|^s_\d\d\d_\d\d\d_|^suite_')
);

$genTemplate = function ($perfData) {
    $caseColors = array ('#DEEBF7','#C6DBEF','#9ECAE1','#6BAED6','#4292C6','#2171B5','#08519C','#08306B');
    $stepColors = array ('#9E0142','#D53E4F','#F46D43','#FDAE61','#FEE08B','#E6F598','#ABDDA4','#66C2A5','#3288BD','#5E4FA2');

    $dashboard = \histou\grafana\dashboard\DashboardFactory::generateDashboard($perfData['host'].' '.$perfData['service']);
    $dashboard->addDefaultAnnotations($perfData['host'], $perfData['service']);
    $dashboard->addAnnotation(
        "errors",
        "SELECT path FROM images WHERE host = '".$perfData['host']."' AND service = '".$perfData['service']."' AND \$timeFilter ORDER BY time DESC LIMIT 100",
        "",
        "path",
        "",
        '#751975',
        '#751975',
        "sakuli"
    );
    $templateName = 'Case';
    $dashboard->addTemplateForPerformanceLabel(
        $templateName,
        $perfData['host'],
        $perfData['service'],
        $regex = '/^c_(\d\d\d)/',
        $multiFormat = true,
        $includeAll = false
    );
    $templateVariableString = $dashboard->genTemplateVariable($templateName);

    $caseLabels = array();
    $caseNames = array();
    $steps = array();
    $maxSteps = 0;
    foreach ($perfData['perfLabel'] as $key => $values) {
        if (preg_match(';suite_([^_].*);', $key, $hit)) {
            $suiteLabel = $hit[0];
            $suiteName = $hit[1];
            continue;
        }
        if (preg_match(';c_\d\d\d_([^_].*);', $key, $hit)) {
            array_push($caseLabels, $hit[0]);
            array_push($caseNames, $hit[1]);
            continue;
        }
        if (preg_match(';s_(\d\d\d)_[^_].*;', $key, $hit)) {
            if (!array_key_exists($hit[1], $steps)) {
                $steps[$hit[1]] = 0;
            }
            $steps[$hit[1]] += 1;
            if ($steps[$hit[1]] > $maxSteps) {
                $maxSteps = $steps[$hit[1]];
            }
            continue;
        }
    }


    //Suite Row
    $suiteRow = new \histou\grafana\Row("Suite-Runtime");
    //Suite Panel
    $suitePanel = \histou\grafana\graphpanel\GraphPanelFactory::generatePanel($perfData['service']." (Sakuli suite $suiteName) on ".$perfData['host']);
    $suitePanel->setLeftUnit("s");
    $suitePanel->setSpan(11);
    $suitePanel->setLeftYAxisMinMax(0);
    $suitePanel->fillBelowLine("/^(?!warning|critical).*$/", 5);
    $suitePanel->setLegend(true, true, true, true, false, false, true, true, true, true);
    $suitePanel->addTarget($suitePanel->genTargetSimple($perfData['host'], $perfData['service'], $perfData['command'], $suiteLabel, $caseColors[0], $suiteName));
    $c = 1;
    for ($i = 0; $i < sizeof($caseLabels); $i++) {
        $suitePanel->addTarget($suitePanel->genTargetSimple($perfData['host'], $perfData['service'], $perfData['command'], $caseLabels[$i], $caseColors[$c], $caseNames[$i]));
        $suitePanel->stack($caseNames[$i].'-value');
        $c = ($c + 1) % (sizeof($caseColors) - 1);
    }
    $suitePanel->addTarget($suitePanel->genTargetSimple($perfData['host'], $perfData['service'], $perfData['command'], 'suite__warning', '#FFFC15', 'warning'));
    $suitePanel->addTarget($suitePanel->genTargetSimple($perfData['host'], $perfData['service'], $perfData['command'], 'suite__critical', '#FF3727', 'critical'));
    $suiteRow->addPanel($suitePanel);
    //Suite Stat Panel
    $suiteStat = \histou\grafana\singlestatpanel\SinglestatPanelFactory::generatePanel("");
    $suiteStat->setSpan(1);
    $suiteStat->addTarget($suiteStat->genTargetSimple($perfData['host'], $perfData['service'], $perfData['command'], 'suite__state'));
    $suiteStat->setColor(array('#99ff66', '#ffc125', '#ee0000'));
    $suiteStat->setThresholds("1", "2");
    $suiteStat->addRangeToTextElement(0, 0.5, 'OK');
    $suiteStat->addRangeToTextElement(0.5, 1.5, 'Warn');
    $suiteStat->addRangeToTextElement(1.5, 2.5, 'Crit');
    $suiteStat->addRangeToTextElement(2.5, 3.5, 'Unkn');
    $suiteRow->addPanel($suiteStat);

    $dashboard->addRow($suiteRow);

    //Case Row
    $caseRow = new \histou\grafana\Row("Case-Runtime");
    $caseRow->setCustomProperty("repeat", $templateName);
    //Case Graph Panel
    $casePanel = \histou\grafana\graphpanel\GraphPanelFactory::generatePanel($perfData['service']." (Sakuli case #$templateVariableString) on ".$perfData['host']);
    $casePanel->setLeftUnit("s");
    $casePanel->setSpan(11);
    $casePanel->setLeftYAxisMinMax(0);
    $casePanel->fillBelowLine("/^(?!warning|critical).*$/", 5);
    $casePanel->setLegend(true, true, true, true, false, false, true, true, true, true);
    $target1 = $casePanel->createTarget(array('host' => array('value' => $perfData['host'], 'operator' => '='),
                                    'service' => array('value' => $perfData['service'], 'operator' => '='),
                                    'command' => array('value' => $perfData['command'], 'operator' => '='),
                                    'performanceLabel' => array('value' => \histou\helper\str::genRegex('c_'.$templateVariableString.'_[^_].*'), 'operator' => '=~')
                                    ));
    $target1 = $casePanel->addXToTarget($target1, array('value'), '', '');
    $target1['alias'] = '$tag_performanceLabel';
    $target1['groupBy'] = array( array("params"=>array("\$interval"), "type"=> "time"),
                                 array("params"=>array("performanceLabel"), "type"=> "tag"),
                                 array("params"=>array("null"), "type"=> "fill"));
    $casePanel->addTarget($target1);
    $target = $casePanel->createTarget(array('host' => array('value' => $perfData['host'], 'operator' => '='),
                                    'service' => array('value' => $perfData['service'], 'operator' => '='),
                                    'command' => array('value' => $perfData['command'], 'operator' => '='),
                                    'performanceLabel' => array('value' => \histou\helper\str::genRegex('s_'.$templateVariableString.'.*'), 'operator' => '=~')
                                    ));
    $target = $casePanel->addXToTarget($target, array('value'), '', '');
    $target['alias'] = '$tag_performanceLabel';
    $target['groupBy'] = array(     array("params"=>array("\$interval"), "type"=> "time"),
                                array("params"=>array("performanceLabel"), "type"=> "tag"),
                                array("params"=>array("null"), "type"=> "fill"));
    $casePanel->addTarget($target);
    $casePanel->stack('/s_\d\d\d_.*/');
    $casePanel->addTarget($casePanel->genTargetSimple($perfData['host'], $perfData['service'], $perfData['command'], "c_$templateVariableString"."__warning", '#FFFC15', 'warning'));
    $casePanel->addTarget($casePanel->genTargetSimple($perfData['host'], $perfData['service'], $perfData['command'], "c_$templateVariableString"."__critical", '#FF3727', 'critical'));
    $c = 0;
    for ($i = 0; $i < $maxSteps; $i++) {
        $casePanel->addRegexColor(sprintf('/s_\d\d\d_%03d_.*/', $i), $stepColors[$c]);
        $c = ($c + 1) % (sizeof($stepColors) - 1);
    }
    $caseRow->addPanel($casePanel);

    //Case Stat Panel
    $caseStat = \histou\grafana\singlestatpanel\SinglestatPanelFactory::generatePanel("");
    $caseStat->setSpan(1);
    $caseStat->addTarget($caseStat->genTargetSimple($perfData['host'], $perfData['service'], $perfData['command'], "c_$templateVariableString".'__state'));
    $caseStat->setColor(array('#99ff66', '#ffc125', '#ee0000'));
    $caseStat->setThresholds("1", "2");
    $caseStat->addRangeToTextElement(0, 0.5, 'OK');
    $caseStat->addRangeToTextElement(0.5, 1.5, 'Warn');
    $caseStat->addRangeToTextElement(1.5, 2.5, 'Crit');
    $caseStat->addRangeToTextElement(2.5, 3.5, 'Unkn');
    $caseRow->addPanel($caseStat);

    $dashboard->addRow($caseRow);

    return $dashboard;
};
