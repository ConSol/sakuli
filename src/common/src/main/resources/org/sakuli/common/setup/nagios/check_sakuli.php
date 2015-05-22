<?php
# PNP template for Sakuli checks 
# Copyright (C) 2015 The Sakuli Team, <sakuli@consol.de>
# See https://github.com/ConSol/sakuli for more information. 

#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.

isset($_GET['debug']) ? $DEBUG = $_GET['debug'] : $DEBUG = 0;

# Position vars
$perf_pos_suite_state = 1;
$perf_pos_suite_runtime = 2;


$col_invisible = '#00000000';

$col_suite_runtime_line = '#636363';
$col_suite_runtime_area = '#bdbdbd';

# Case colors
$col_case_line = $this->config->scheme['Blues'];
$col_case_line = array_merge($col_case_line, $col_case_line, $col_case_line, $col_case_line);
$col_case_area = $col_case_line;
$col_case_area_opacity = "BB";

# Step colors

$col_step_line = $this->config->scheme['Spectral'];
$col_step_line = array_merge($col_step_line, $col_step_line, $col_step_line, $col_step_line);
$col_step_area = $col_step_line;
$col_step_area_opacity = "BB";

# State colors
$col_OK = "#008500";
$col_WARN = "#ffcc00";
$col_CRIT = "#d30000";
$col_UNKN = "#d6d6d6";
$col_NOK = "#ff8000";

# CPU Usage color
$col_cpu = "#ffff0099";

# Memory Usage color
$col_mem = "#00b30099";

# Not-OK Ticker
$ticker_frac = "-0.04";
$ticker_opacity = "BB";
$ticker_dist_factor = "1.05";

# Unknown Ticker
$unkn_tick_frac = "1.0";
$unkn_tick_opacity = "FF";

sort($this->DS);

$suitename = preg_replace('/^suite_(.*)$/', '$1', $NAME[$perf_pos_suite_runtime]);

## Determine length of all labels ############################################
$label_max_length = 0;
$labels = array();

# Loop over case names
foreach($this->DS as $k=>$v) {
        if (preg_match('/(c|s)_(\d+)_(\d+_)?([a-zA-Z0-9].*)/', $v["LABEL"], $matches)) {
		array_push($labels, strlen($matches[4]));
	}
}
array_push($labels, strlen($suitename));
$label_max_length = max($labels);

## CPU/MEMORY GRAPHS ###########################################################
# show CPU/MEM graphs only if Macros are set properly. For more information, see
# https://github.com/ConSol/sakuli/blob/master/docs/installation-omd.md#include-cpumem-graphs-in-sakuli-graphs-optional
if ( ( (array_key_exists('E2ECPUHOST', $this->MACRO)) and ($this->MACRO['E2ECPUHOST'] != '$_HOSTE2E_CPU_HOST$')) and ( ((array_key_exists('E2ECPUSVC', $this->MACRO))) and ($this->MACRO['E2ECPUSVC'] != '$_HOSTE2E_CPU_SVC$'))) {
	if (preg_match('/usage/i', $this->MACRO['E2ECPUSVC'])) {
        	$graph_cpu = "%";
	        $rrddef_cpu = rrd::def("cpu_usage", OMD_SITE_ROOT . "/var/pnp4nagios/perfdata/" .
	                $this->MACRO['E2ECPUHOST'] . "/" .
	                $this->MACRO['E2ECPUSVC'] . ".rrd",1,"AVERAGE");
	        $rrddef_cpu .= rrd::line1("cpu_usage", $col_cpu, pad("CPU Usage", $label_max_length));
	        $rrddef_cpu .= rrd::gprint("cpu_usage", "MAX", "%3.2lf%%  MAX ");
	        $rrddef_cpu .= rrd::gprint("cpu_usage", "AVERAGE", "%3.2lf%%  AVG ");
	        $rrddef_cpu .= rrd::gprint("cpu_usage", "LAST", "%3.2lf%%  LAST \j");
	} else if (preg_match('/load/i', $this->MACRO['E2ECPUSVC'])) {
        	$graph_cpu = "load";
                $rrddef_cpu = rrd::def("cpu_load", OMD_SITE_ROOT . "/var/pnp4nagios/perfdata/" .
                        $this->MACRO['E2ECPUHOST'] . "/" .
                        $this->MACRO['E2ECPUSVC'] . ".rrd",1,"AVERAGE");
		# Load is usually a much lower value than usage (%) -> multiply by 10 and scale right axis
		$rrddef_cpu .= rrd::cdef("cpu_load10", "cpu_load,10,*");
                $rrddef_cpu .= rrd::line1("cpu_load10", $col_cpu, pad("CPU Load", $label_max_length));
                $rrddef_cpu .= rrd::gprint("cpu_load", "MAX", "%3.2lf MAX ");
                $rrddef_cpu .= rrd::gprint("cpu_load", "AVERAGE", "%3.2lf AVG ");
                $rrddef_cpu .= rrd::gprint("cpu_load", "LAST", "%3.2lf LAST \j");
	}
} else {
        $graph_cpu = false;
        $rrdopts_cpu = "";
}
if ( ( (array_key_exists('E2EMEMHOST', $this->MACRO)) and ($this->MACRO['E2EMEMHOST'] != '$_HOSTE2E_MEM_HOST$')) and ( ((array_key_exists('E2EMEMSVC', $this->MACRO))) and ($this->MACRO['E2EMEMSVC'] != '$_HOSTE2E_MEM_SVC$'))) {
        $graph_mem = true;
        $rrddef_mem = rrd::def("mem_usage", OMD_SITE_ROOT ."/var/pnp4nagios/perfdata/" .
                $this->MACRO['E2EMEMHOST'] . "/" .
                $this->MACRO['E2EMEMSVC'] . "_physical_memory_%.rrd",1,"AVERAGE");
        $rrddef_mem .= rrd::line1("mem_usage", $col_mem, "phys. Memory Usage");
        $rrddef_mem .= rrd::gprint("mem_usage", "MAX", "%3.2lf%% MAX ");
        $rrddef_mem .= rrd::gprint("mem_usage", "AVERAGE", "%3.2lf%% AVG ");
        $rrddef_mem .= rrd::gprint("mem_usage", "LAST", "%3.2lf%% LAST \j");
} else {
        $graph_mem = false;
        $rrdopts_mem = "";
}

## SUITE Graph  #############################################################

$ds_name[0] = "Sakuli Suite '" . $suitename . "'";
$opt[0] = "--vertical-label \"seconds\"  -l 0 --slope-mode --title \"$servicedesc (Sakuli Suite $suitename) on $hostname\" ";
$def[0] = "";

# AREA  ---------------------------------------------------------------------
foreach($this->DS as $k=>$v) {
	# c_001_case1
	# but do not match a _state_ label like 'c_001__state_demo_win7' (which contains two backslashes)
	if (preg_match('/c_(\d+)_([a-zA-Z0-9].*)/', $v["LABEL"], $c_matches)) {
		$casecount = $c_matches[1];
		$casecount_int = intval($casecount);
		$casename = $c_matches[2];
		$def[0] .= rrd::def("c_area$casecount", $v["RRDFILE"], $v["DS"], "AVERAGE");
		if ($casecount == "001") {
			$def[0] .= rrd::comment("Sakuli Cases\: \\n");
			$def[0] .= rrd::cdef("c_area_stackbase$casecount", "c_area$casecount,1,*");
			$def[0] .= rrd::area("c_area$casecount", $col_case_area[$casecount_int].$col_case_area_opacity, pad($casename, $label_max_length), 0);
		} else {
			# all areas >1 are stacked upon a invisible line 
			$def[0] .= rrd::line1("c_area_stackbase".lead3($casecount_int-1),"#00000000");
			$def[0] .= rrd::area("c_area$casecount", $col_case_area[$casecount_int].$col_case_area_opacity, $casename, 1);
			# add value to stackbase
			$def[0] .= rrd::cdef("c_area_stackbase$casecount", "c_area_stackbase".lead3($casecount_int-1).",c_area$casecount,+");
		}

		$def[0] .= rrd::gprint("c_area$casecount", "LAST", "%3.2lf s LAST");
		$def[0] .= rrd::gprint("c_area$casecount", "MAX", "%3.2lf s MAX ");
		$def[0] .= rrd::gprint("c_area$casecount", "AVERAGE", "%3.2lf s AVG \j");

	}
}



# LINE ---------------------------------------------------------------------
$c_last_index = "";
foreach($this->DS as $k=>$v) {
	# c_001_case1
	# do not match a state label like 'c_1__state_demo_win7' (which contains two backslashes)
	if (preg_match('/c_(\d+)_([a-zA-Z0-9].*)/', $v["LABEL"], $c_matches)) {
		$casecount = $c_matches[1];
		$casecount_int = intval($casecount);
		$casename = $c_matches[2];
		$def[0] .= rrd::def("c_line$casecount", $v["RRDFILE"], $v["DS"], "AVERAGE");
		if ($casecount == "001") {
			$def[0] .= rrd::cdef("c_line_stackbase$casecount", "c_line$casecount,1,*");
			$def[0] .= rrd::line1("c_line$casecount", $col_case_line[$casecount_int], "", 0);
		} else {
			# invisible line to stack upon
			$def[0] .= rrd::line1("c_line_stackbase".lead3($casecount_int-1),"#00000000");
			$def[0] .= rrd::line1("c_line$casecount", $col_case_area[$casecount_int], "", 1);
			# add value to stackbase
			$def[0] .= rrd::cdef("c_line_stackbase$casecount", "c_line_stackbase".lead3($casecount_int-1).",c_line$casecount,+");
		}
		# is this a unknown value? 
		$def[0] .= rrd::cdef("c_".$casecount."_unknown", "c_line$casecount,UN,1,0,IF");
		$c_last_index = $casecount;
	}
}	

$def[0] .= rrd::comment(" \\n");
$def[0] .= rrd::comment("Sakuli Suite\g");
if(($WARN[$perf_pos_suite_runtime] != "") && ($CRIT[$perf_pos_suite_runtime] != "")) {
	$def[0] .= rrd::comment(" (\g");
	$def[0] .= rrd::hrule($WARN[$perf_pos_suite_runtime], $col_WARN, "Warning  ".$WARN[$perf_pos_suite_runtime].$UNIT[$perf_pos_suite_runtime]);
	$def[0] .= rrd::hrule($CRIT[$perf_pos_suite_runtime], $col_CRIT, "Critical  ".$CRIT[$perf_pos_suite_runtime].$UNIT[$perf_pos_suite_runtime]."\g");
	$def[0] .= rrd::comment(")\g");
} 

$def[0] .= rrd::comment("\:\\n");
$def[0] .= rrd::def("suite", $RRDFILE[$perf_pos_suite_runtime], $DS[$perf_pos_suite_runtime], "AVERAGE");
if ($c_last_index != "") {
	$def[0] .= rrd::cdef("suite_diff", "suite,c_line_stackbase".$c_last_index.",UN,0,c_line_stackbase".$c_last_index.",IF,-");
	# invisible line to stack upon
	$def[0] .= rrd::line1("c_line_stackbase".($c_last_index),"#00000000");
	$def[0] .= rrd::area("suite_diff", $col_suite_runtime_area,pad($suitename, $label_max_length),1 );
	# invisible line to stack upon
	$def[0] .= rrd::line1("c_line_stackbase".($c_last_index),"#00000000");
	$def[0] .= rrd::line1("suite_diff", $col_suite_runtime_line, "",1 );
} else {
	# no cases, no STACKing
	$def[0] .= rrd::area("suite", $col_suite_runtime_area,$suitename );
	$def[0] .= rrd::line1("suite", $col_suite_runtime_line, "" );
}

$def[0] .= rrd::gprint("suite", "LAST", "%3.2lf ".$UNIT[$perf_pos_suite_runtime]." LAST");
$def[0] .= rrd::gprint("suite", "MAX", "%3.2lf ".$UNIT[$perf_pos_suite_runtime]." MAX ");
$def[0] .= rrd::gprint("suite", "AVERAGE", "%3.2lf ".$UNIT[$perf_pos_suite_runtime]." AVG \j");

$def[0] .= rrd::comment(" \\n");
# invisible line above maximum (for space between MAX and TICKER) -------------------------------------	
$def[0] .= rrd::def("suite_max", $RRDFILE[$perf_pos_suite_runtime], $DS[$perf_pos_suite_runtime], "MAX") ;
$def[0] .= rrd::cdef("suite_maxplus", "suite_max,".$ticker_dist_factor.",*");
$def[0] .= rrd::line1("suite_maxplus", $col_invisible);
# TICKER ---------------------------------------------------------------------
$def[0] .= rrd::def("suite_state", $RRDFILE[$perf_pos_suite_state], $DS[$perf_pos_suite_state], "MAX") ;
$def[0] .= rrd::cdef("suite_state_unknown", "suite_state,2,GT,suite_state,0,IF") ;
$def[0] .= rrd::cdef("suite_state_nok", "suite_state,0,GT,suite_state,0,IF") ;
$def[0] .= rrd::cdef("suite_state_nok2", "suite_state_nok,3,LT,suite_state_nok,0,IF") ;
$def[0] .= "TICK:suite_state_nok2".$col_NOK.$ticker_opacity.":".$ticker_frac.":not_ok " ;
$def[0] .= "TICK:suite_state_unknown".$col_UNKN.$unkn_tick_opacity.":".$unkn_tick_frac.":unknown/stale " ;
for ($i=1; $i <= intval($c_last_index); $i++) {
	$def[0] .= "TICK:c_".lead3($i)."_unknown".$col_UNKN.$unkn_tick_opacity.":".$unkn_tick_frac.": " ;
}
$def[0] .= "VRULE:".$NAGIOS_TIMET."#000000:\"Last Service Check \\n\" ";

if ($graph_cpu or $graph_mem) {
	$def[0] .= rrd::comment(" \\n");
	$def[0] .= rrd::comment("Host Statistics\:\\n");
	if ($graph_cpu == "load" ) {
		# Load is usually a much lower value than usage (%) -> scale the right axis with factor 10
		$opt[0] .= " --right-axis \"0.1:0\" --right-axis-label \"CPU Load\" ";
	} else {
		$opt[0] .= " --right-axis \"1:0\" --right-axis-label \"CPU Usage\" ";
	}
}
if ( $graph_cpu ) {
	$def[0] .= $rrddef_cpu;	
}
if ( $graph_mem ) {
	$def[0] .= $rrddef_mem;	
}

# CASE Graphs  #############################################################
foreach ($this->DS as $KEY=>$VAL) {
	# c_001_case1
	if(preg_match('/^c_(\d+)_(.*)/', $VAL['LABEL'], $c_matches)) {
		$casecount = $c_matches[1];
		$casecount_int = intval($casecount);
		$casename = $c_matches[2];
		$ds_name[$casecount_int] = "Sakuli Case $casename";
		$opt[$casecount_int] = "--vertical-label \"seconds\"  -l 0 -M --slope-mode --title \"$servicedesc (Sakuli case $casecount_int) on $hostname\" ";
		$def[$casecount_int] = "";
		# STEP AREA ---------------------------------------------------------------------
		foreach ($this->DS as $k=>$v) {
			# s_001_001_stepone
			# s_001_002_steptwo
			# ...
			if (preg_match('/^s_'.$casecount.'_(\d+)_(.*)/', $v['LABEL'], $s_matches)) {
				$stepcount = $s_matches[1];
				$stepcount_int = intval($stepcount);
				$stepname = $s_matches[2];
				$def[$casecount_int] .= rrd::def("s_area$stepcount", $v['RRDFILE'], $v['DS'], "AVERAGE");

				if ($stepcount == "001"){
					# first step
					$def[$casecount_int] .= rrd::comment("Steps\: \\n");
					$def[$casecount_int] .= rrd::cdef("s_area_stackbase$stepcount", "s_area$stepcount,1,*");
	        			$def[$casecount_int] .= rrd::area("s_area$stepcount", $col_step_area[$stepcount_int].$col_step_area_opacity,pad($stepname, $label_max_length), 0 );
				} else {
					# all areas >1 are stacked upon a invisible line 
					$def[$casecount_int] .= rrd::line1("s_area_stackbase".lead3($stepcount-1),"#00000000");	
					$def[$casecount_int] .= rrd::area("s_area$stepcount", $col_step_area[$stepcount_int].$col_step_area_opacity,pad($stepname, $label_max_length), 1 );
					# add value to s_area_stackbase
					$def[$casecount_int] .= rrd::cdef("s_area_stackbase$stepcount", "s_area_stackbase".lead3($stepcount_int-1).",s_area$stepcount,+");
				}
				$def[$casecount_int] .= rrd::gprint("s_area$stepcount", "LAST", "%3.2lf s LAST");
				$def[$casecount_int] .= rrd::gprint("s_area$stepcount", "MAX", "%3.2lf s MAX ");
				$def[$casecount_int] .= rrd::gprint("s_area$stepcount", "AVERAGE", "%3.2lf s AVG \j");
			}
		}
		# invisible line above maximum (for space between MAX and TICKER) ---------------	
		$def[$casecount_int] .= rrd::def("case".$casecount."_max", $VAL['RRDFILE'], $VAL['DS'], "MAX") ;
		$def[$casecount_int] .= rrd::cdef("case".$casecount."_maxplus", "case".$casecount."_max,".$ticker_dist_factor.",*");
		$def[$casecount_int] .= rrd::line1("case".$casecount."_maxplus", $col_invisible);
		# STEP LINE ---------------------------------------------------------------------
		$s_last_index = "";
		foreach ($this->DS as $k=>$v) {
			# s_001_001_stepone
                        # s_001_002_steptwo
                        # ...
			if (preg_match('/^s_'.$casecount.'_(\d+)_(.*)/', $v['LABEL'], $s_matches)) {
				$stepcount = $s_matches[1];
				$stepcount_int = intval($stepcount);
				$stepname = $s_matches[2];
				$def[$casecount_int] .= rrd::def("s_line$stepcount", $v['RRDFILE'], $v['DS'], "AVERAGE");
				if ($stepcount == "001"){
					$def[$casecount_int] .= rrd::cdef("s_line_stackbase$stepcount", "s_line$stepcount,1,*");
					$def[$casecount_int] .= rrd::line1("s_line$stepcount", $col_step_line[$stepcount_int], "", 0 );
				} else {
					# invisible line to stack upon
					$def[$casecount_int] .= rrd::line1("s_line_stackbase".lead3($stepcount_int-1),"#00000000");	
					$def[$casecount_int] .= rrd::line1("s_line$stepcount", $col_step_line[$stepcount_int], "", 1 );
					# add value to s_line_stackbase
					$def[$casecount_int] .= rrd::cdef("s_line_stackbase$stepcount", "s_line_stackbase".lead3($stepcount_int-1).",s_line$stepcount,+");
				}
				$s_last_index = $stepcount;
			}
		}
		# CASE Warn/Crit -----------------------------------------------------------------
		$def[$casecount_int] .= rrd::comment(" \\n");
		$def[$casecount_int] .= rrd::comment("Case ".$casecount_int ."\g");
		if(($VAL["WARN"] != "") && ($VAL["CRIT"] != "")) {
			$def[$casecount_int] .= rrd::comment(" (\g");
			$def[$casecount_int] .= rrd::hrule($VAL["WARN"], "#FFFF00", "Warning  ".$VAL["WARN"].$UNIT[$casecount_int]);
			$def[$casecount_int] .= rrd::hrule($VAL["CRIT"], "#FF0000", "Critical  ".$VAL["CRIT"].$UNIT[$casecount_int]."\g");
			$def[$casecount_int] .= rrd::comment(")\g");
		}
		# CASE LINE & AREA --------------------------------------------------------------
		$def[$casecount_int] .= rrd::comment("\:\\n");
	        $def[$casecount_int] .= rrd::def("case$casecount", $VAL['RRDFILE'], $VAL['DS'], "AVERAGE");
		# is this a unknown value?
		$def[$casecount_int] .= rrd::cdef("case".$casecount."_unknown", "case$casecount,UN,1,0,IF");
		if ($s_last_index != "") {
			$def[$casecount_int] .= rrd::cdef("case_diff$casecount","case$casecount,s_line_stackbase$s_last_index,-");
			# invisible line to stack upon
			$def[$casecount_int] .= rrd::line1("s_line_stackbase$s_last_index","#00000000");	
			$def[$casecount_int] .= rrd::area   ("case_diff$casecount", $col_case_area[$casecount_int].$col_case_area_opacity, pad($casename,$label_max_length),1 );
			# invisible line to stack upon
			$def[$casecount_int] .= rrd::line1("s_line_stackbase$s_last_index","#00000000");	
			$def[$casecount_int] .= rrd::line1   ("case_diff$casecount", $col_case_line[$casecount_int],"",1);
		} else {
			# no steps, no stacks
			$def[$casecount_int] .= rrd::area   ("case$casecount", $col_case_area[$casecount_int].$col_case_area_opacity, $casename );
			$def[$casecount_int] .= rrd::line1   ("case$casecount", $col_case_line[$casecount_int],"");
		}
		$def[$casecount_int] .= rrd::gprint ("case$casecount", "LAST", "%3.2lf s LAST");
		$def[$casecount_int] .= rrd::gprint ("case$casecount", "MAX", "%3.2lf s MAX ");
		$def[$casecount_int] .= rrd::gprint ("case$casecount", "AVERAGE", "%3.2lf s AVG \j");
		$def[$casecount_int] .= rrd::comment(" \\n");
		# TICKS ---------------------------------------------------------------------
		foreach ($this->DS as $k=>$v) {
			if(preg_match('/^c_'.$casecount.'__state/', $v['LABEL'], $state_matches)) {
				$def[$casecount_int] .= rrd::def("case".$casecount."_state", $v['RRDFILE'], $v['DS'], "MAX") ;
				$def[$casecount_int] .= rrd::cdef("case".$casecount."_state_unknown", "case".$casecount."_state,2,GT,case".$casecount."_state,0,IF") ;
				$def[$casecount_int] .= rrd::cdef("case".$casecount."_state_nok", "case".$casecount."_state,0,GT,case".$casecount."_state,0,IF") ;
				$def[$casecount_int] .= rrd::cdef("case".$casecount."_state_nok2", "case".$casecount."_state_nok,3,LT,case".$casecount."_state_nok,0,IF") ;
				$def[$casecount_int] .= "TICK:case".$casecount."_state_nok2".$col_NOK.$ticker_opacity.":".$ticker_frac.":not_ok " ;
				$def[$casecount_int] .= "TICK:case".$casecount."_state_unknown".$col_UNKN.$unkn_tick_opacity.":".$unkn_tick_frac.": " ;
			}
		}
		$def[$casecount_int] .= "TICK:case".$casecount."_unknown".$col_UNKN.$unkn_tick_opacity.":".$unkn_tick_frac.":unknown/stale " ;
		$def[$casecount_int] .= "VRULE:".$NAGIOS_TIMET."#000000:\"Last Service Check \\n\" ";

                if ($graph_cpu or $graph_mem) {
                        $def[$casecount_int] .= rrd::comment(" \\n");
                        $def[$casecount_int] .= rrd::comment("Host Statistics\:\\n");

		        if ($graph_cpu == "load" ) {
				# Load is usually a much lower value than usage (%) -> scale the right axis with factor 10
		                $opt[$casecount_int] .= " --right-axis \"0.1:0\" --right-axis-label \"CPU Load\" ";
		        } else {
		                $opt[$casecount_int] .= " --right-axis \"1:0\" --right-axis-label \"CPU Usage\" ";
		        }
                }
		if ( $graph_cpu ) {
			$def[$casecount_int] .= $rrddef_cpu;	
		}
 		if ( $graph_mem ) {
			$def[$casecount_int] .= $rrddef_mem;	
		}

	}
}

# Pad the string with spaces to ensure column alignment
function pad ($str, $len) {
	$padding = $len - strlen($str);
	return $str . str_repeat(" ", $padding);
}


# refill numbers with leading 0s
function lead3 ($num) {
	return str_pad($num,3,'0',STR_PAD_LEFT);
}

if ( $DEBUG == 1 ) {
#throw new Kohana_exception(print_r($def,TRUE));
#throw new Kohana_exception(print_r($idxm1,TRUE));
}

