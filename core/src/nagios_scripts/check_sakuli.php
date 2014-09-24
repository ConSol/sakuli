<?php
# Copyright (C) 2012  Simon Meggle, <simon.meggle@consol.de>

# this program Is free software; you can redistribute it And/Or
# modify it under the terms of the GNU General Public License
# As published by the Free Software Foundation; either version 2
# of the License, Or (at your Option) any later version.

# this program Is distributed In the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY Or FITNESS For A PARTICULAR PURPOSE.  See the
# GNU General Public License For more details.

# You should have received a copy of the GNU General Public License
# along With this program; If Not, write To the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

# PNP template for Sakuli checks with check_mysql_health and the
# Perl module CheckMySQLHealthSakuli.pm. 

isset($_GET['debug']) ? $DEBUG = $_GET['debug'] : $DEBUG = 0;

# Position vars
$perf_pos_suite_state = 1;
$perf_pos_suite_runtime = 2;


$col_invisible = '#00000000';

$col_suite_runtime_line = '#636363';
$col_suite_runtime_area = '#bdbdbd';

# Case colors
$col_case_line = $this->config->scheme['Blues'];
$col_case_area = $col_case_line;
$col_case_area_opacity = "BB";

# Step colors

$col_step_line = $this->config->scheme['Spectral'];
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


# fixme: nur einmal
	#$rrdopts_mem = "--right-axis \"1:0\" --right-axis-label \"CPU Usage %\" ";
if ( ( $this->MACRO['E2ECPUHOST'] != '$_HOSTE2E_CPU_HOST$') and ( $this->MACRO['E2ECPUSVC'] != '$_HOSTE2E_CPU_SVC$')) {
	$graph_cpu = true;
	$rrddef_cpu = rrd::def("cpu_usage", OMD_SITE_ROOT . "/var/pnp4nagios/perfdata/" .  
		$this->MACRO['E2ECPUHOST'] . "/" . 
		$this->MACRO['E2ECPUSVC'] . "_1.rrd",1,"AVERAGE");
	$rrddef_cpu .= rrd::line1("cpu_usage", $col_cpu, "CPU Usage");
	$rrddef_cpu .= rrd::gprint("cpu_usage", "MAX", "%3.2lf%% MAX");
	$rrddef_cpu .= rrd::gprint("cpu_usage", "AVERAGE", "%3.2lf%% AVERAGE");
	$rrddef_cpu .= rrd::gprint("cpu_usage", "LAST", "%3.2lf%% LAST \j");
} else {
	$graph_cpu = false;
	$rrdopts_cpu = "";
}
if ( ( $this->MACRO['E2EMEMHOST'] != '$_HOSTE2E_MEM_HOST$') and ( $this->MACRO['E2EMEMSVC'] != '$_HOSTE2E_MEM_SVC$')) {
	$graph_mem = true;
	$rrddef_mem = rrd::def("mem_usage", OMD_SITE_ROOT ."/var/pnp4nagios/perfdata/" .  
		$this->MACRO['E2EMEMHOST'] . "/" . 
		$this->MACRO['E2EMEMSVC'] . "_physical_memory_%.rrd",1,"AVERAGE");
	$rrddef_mem .= rrd::line1("mem_usage", $col_mem, "phys. Memory Usage");
	$rrddef_mem .= rrd::gprint("mem_usage", "MAX", "%3.2lf%% MAX");
	$rrddef_mem .= rrd::gprint("mem_usage", "AVERAGE", "%3.2lf%% AVERAGE");
	$rrddef_mem .= rrd::gprint("mem_usage", "LAST", "%3.2lf%% LAST \j");
} else {
	$graph_mem = false;	
	$rrdopts_mem = "";
}



## SUITE Graph  #############################################################
$suitename = preg_replace('/^suite_(.*)$/', '$1', $NAME[$perf_pos_suite_runtime]);

$ds_name[0] = "Sakuli Suite '" . $suitename . "'";
$opt[0] = "--vertical-label \"seconds\"  -l 0 --slope-mode --title \"$servicedesc (Sakuli Suite $suitename) on $hostname\" ";
$def[0] = "";

# AREA  ---------------------------------------------------------------------
foreach($this->DS as $k=>$v) {
	# do not match a state label like 'c_1__state_demo_win7' (which contains two backslashes)
	if (preg_match('/c_(\d+)_([a-zA-Z0-9].*)/', $v["LABEL"], $c_matches)) {
		$casecount = $c_matches[1];
		$casename = $c_matches[2];
		$def[0] .= rrd::def("c_area$casecount", $v["RRDFILE"], $v["DS"], "AVERAGE");
		if ($casecount == "1") {
			$def[0] .= rrd::comment("Sakuli Cases\: \\n");
			$def[0] .= rrd::cdef("c_area_stackbase$casecount", "c_area$casecount,1,*");
			$def[0] .= rrd::area("c_area$casecount", $col_case_area[$casecount].$col_case_area_opacity, $casename, 0);
		} else {
			# invisible line to stack upon
			$def[0] .= rrd::line1("c_area_stackbase".($casecount-1),"#00000000");
			$def[0] .= rrd::area("c_area$casecount", $col_case_area[$casecount].$col_case_area_opacity, $casename, 1);
			# add value to stackbase
			$def[0] .= rrd::cdef("c_area_stackbase$casecount", "c_area_stackbase".($casecount-1).",c_area$casecount,+");
		}

		$def[0] .= rrd::gprint("c_area$casecount", "LAST", "%3.2lf $UNIT[$casecount] LAST");
		$def[0] .= rrd::gprint("c_area$casecount", "MAX", "%3.2lf $UNIT[$casecount] MAX ");
		$def[0] .= rrd::gprint("c_area$casecount", "AVERAGE", "%3.2lf $UNIT[$casecount] AVERAGE \j");

	}
}



# LINE ---------------------------------------------------------------------
$c_last_index = "";
foreach($this->DS as $k=>$v) {
	# do not match a state label like 'c_1__state_demo_win7' (which contains two backslashes)
	if (preg_match('/c_(\d+)_([a-zA-Z0-9].*)/', $v["LABEL"], $c_matches)) {
		$casecount = $c_matches[1];
		$casename = $c_matches[2];
		$def[0] .= rrd::def("c_line$casecount", $v["RRDFILE"], $v["DS"], "AVERAGE");
		if ($casecount == "1") {
			$def[0] .= rrd::cdef("c_line_stackbase$casecount", "c_line$casecount,1,*");
			$def[0] .= rrd::line1("c_line$casecount", $col_case_line[$casecount], "", 0);
		} else {
			# invisible line to stack upon
			$def[0] .= rrd::line1("c_line_stackbase".($casecount-1),"#00000000");
			$def[0] .= rrd::line1("c_line$casecount", $col_case_area[$casecount], "", 1);
			# add value to stackbase
			$def[0] .= rrd::cdef("c_line_stackbase$casecount", "c_line_stackbase".($casecount-1).",c_line$casecount,+");
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
	$def[0] .= rrd::area("suite_diff", $col_suite_runtime_area,$suitename,1 );
	# invisible line to stack upon
	$def[0] .= rrd::line1("c_line_stackbase".($c_last_index),"#00000000");
	$def[0] .= rrd::line1("suite_diff", $col_suite_runtime_line, "",1 );
} else {
	# no cases, no STACKing
	$def[0] .= rrd::area("suite", $col_suite_runtime_area,$suitename );
	$def[0] .= rrd::line1("suite", $col_suite_runtime_line, "" );
}

$def[0] .= rrd::gprint("suite", "LAST", "%3.2lf ".$UNIT[$perf_pos_suite_runtime]." LAST");
$def[0] .= rrd::gprint("suite", "MAX", "%3.2lf ".$UNIT[$perf_pos_suite_runtime]." MAX");
$def[0] .= rrd::gprint("suite", "AVERAGE", "%3.2lf ".$UNIT[$perf_pos_suite_runtime]." AVERAGE \j");
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
for ($i=1; $i<=$c_last_index; $i++) {
	$def[0] .= "TICK:c_".$i."_unknown".$col_UNKN.$unkn_tick_opacity.":".$unkn_tick_frac.": " ;
}
$def[0] .= "VRULE:".$NAGIOS_TIMET."#000000:\"Last Service Check \\n\" ";

if ($graph_cpu or $graph_mem) {
	$def[0] .= rrd::comment(" \\n");
	$def[0] .= rrd::comment("Host Statistics\:\\n");
	$opt[0] .= " --right-axis \"1:0\" --right-axis-label \"%\" ";
}
if ( $graph_cpu ) {
	$def[0] .= $rrddef_cpu;	
}
if ( $graph_mem ) {
	$def[0] .= $rrddef_mem;	
}

# CASE Graphs  #############################################################
foreach ($this->DS as $KEY=>$VAL) {
	if(preg_match('/^c_(\d?)_(.*)/', $VAL['LABEL'], $c_matches)) {
		$casecount = $c_matches[1];
		$casename = $c_matches[2];
		$ds_name[$casecount] = "Sakuli Case $casename";
		$opt[$casecount] = "--vertical-label \"seconds\"  -l 0 -M --slope-mode --title \"$servicedesc (Sakuli case $casecount) on $hostname\" ";
		$def[$casecount] = "";
		# STEP AREA ---------------------------------------------------------------------
		foreach ($this->DS as $k=>$v) {
			if (preg_match('/^s_'.$casecount.'_(\d?)_(.*)/', $v['LABEL'], $s_matches)) {
				$stepcount = $s_matches[1];
				$stepname = $s_matches[2];
				$def[$casecount] .= rrd::def("s_area$stepcount", $v['RRDFILE'], $v['DS'], "AVERAGE");
				if ($stepcount == "1"){
					$def[$casecount] .= rrd::comment("Steps\: \\n");
					$def[$casecount] .= rrd::cdef("s_area_stackbase$stepcount", "s_area$stepcount,1,*");
	        			$def[$casecount] .= rrd::area("s_area$stepcount", $col_step_area[$stepcount].$col_step_area_opacity,$stepname, 0 );
				} else {
					# invisible line to stack upon
					$def[$casecount] .= rrd::line1("s_area_stackbase".($stepcount-1),"#00000000");	
					$def[$casecount] .= rrd::area("s_area$stepcount", $col_step_area[$stepcount].$col_step_area_opacity,$stepname, 1 );
					# add value to s_area_stackbase
					$def[$casecount] .= rrd::cdef("s_area_stackbase$stepcount", "s_area_stackbase".($stepcount-1).",s_area$stepcount,+");
				}
				$def[$casecount] .= rrd::gprint("s_area$stepcount", "LAST", "%3.2lf $UNIT[$stepcount] LAST");
				$def[$casecount] .= rrd::gprint("s_area$stepcount", "MAX", "%3.2lf $UNIT[$stepcount] MAX ");
				$def[$casecount] .= rrd::gprint("s_area$stepcount", "AVERAGE", "%3.2lf $UNIT[$stepcount] AVERAGE \j");
			}
		}
		# invisible line above maximum (for space between MAX and TICKER) ---------------	
		$def[$casecount] .= rrd::def("case".$casecount."_max", $VAL['RRDFILE'], $VAL['DS'], "MAX") ;
		$def[$casecount] .= rrd::cdef("case".$casecount."_maxplus", "case".$casecount."_max,".$ticker_dist_factor.",*");
		$def[$casecount] .= rrd::line1("case".$casecount."_maxplus", $col_invisible);
		# STEP LINE ---------------------------------------------------------------------
		$s_last_index = "";
		foreach ($this->DS as $k=>$v) {
			if (preg_match('/^s_'.$casecount.'_(\d?)_(.*)/', $v['LABEL'], $s_matches)) {
				$stepcount = $s_matches[1];
				$stepname = $s_matches[2];
				$def[$casecount] .= rrd::def("s_line$stepcount", $v['RRDFILE'], $v['DS'], "AVERAGE");
				if ($stepcount == "1"){
					$def[$casecount] .= rrd::cdef("s_line_stackbase$stepcount", "s_line$stepcount,1,*");
					$def[$casecount] .= rrd::line1("s_line$stepcount", $col_step_line[$stepcount], "", 0 );
				} else {
					# invisible line to stack upon
					$def[$casecount] .= rrd::line1("s_line_stackbase".($stepcount-1),"#00000000");	
					$def[$casecount] .= rrd::line1("s_line$stepcount", $col_step_line[$stepcount], "", 1 );
					# add value to s_line_stackbase
					$def[$casecount] .= rrd::cdef("s_line_stackbase$stepcount", "s_line_stackbase".($stepcount-1).",s_line$stepcount,+");
				}
				$s_last_index = $stepcount;
			}
		}
		# CASE Warn/Crit -----------------------------------------------------------------
		$def[$casecount] .= rrd::comment(" \\n");
		$def[$casecount] .= rrd::comment("Case ".$casecount ."\g");
		if(($VAL["WARN"] != "") && ($VAL["CRIT"] != "")) {
			$def[$casecount] .= rrd::comment(" (\g");
			$def[$casecount] .= rrd::hrule($VAL["WARN"], "#FFFF00", "Warning  ".$VAL["WARN"].$UNIT[$casecount]);
			$def[$casecount] .= rrd::hrule($VAL["CRIT"], "#FF0000", "Critical  ".$VAL["CRIT"].$UNIT[$casecount]."\g");
			$def[$casecount] .= rrd::comment(")\g");
		}
		# CASE LINE & AREA --------------------------------------------------------------
		$def[$casecount] .= rrd::comment("\:\\n");
	        $def[$casecount] .= rrd::def("case$casecount", $VAL['RRDFILE'], $VAL['DS'], "AVERAGE");
		# is this a unknown value?
		$def[$casecount] .= rrd::cdef("case".$casecount."_unknown", "case$casecount,UN,1,0,IF");
		if ($s_last_index != "") {
			$def[$casecount] .= rrd::cdef("case_diff$casecount","case$casecount,s_line_stackbase$s_last_index,-");
			# invisible line to stack upon
			$def[$casecount] .= rrd::line1("s_line_stackbase$s_last_index","#00000000");	
			$def[$casecount] .= rrd::area   ("case_diff$casecount", $col_case_area[$casecount].$col_case_area_opacity, $casename,1 );
			# invisible line to stack upon
			$def[$casecount] .= rrd::line1("s_line_stackbase$s_last_index","#00000000");	
			$def[$casecount] .= rrd::line1   ("case_diff$casecount", $col_case_line[$casecount],"",1);
		} else {
			# no steps, no stacks
			$def[$casecount] .= rrd::area   ("case$casecount", $col_case_area[$casecount].$col_case_area_opacity, $casename );
			$def[$casecount] .= rrd::line1   ("case$casecount", $col_case_line[$casecount],"");
		}
		$def[$casecount] .= rrd::gprint ("case$casecount", "LAST", "%3.2lf $UNIT[$casecount] LAST");
		$def[$casecount] .= rrd::gprint ("case$casecount", "MAX", "%3.2lf $UNIT[$casecount] MAX");
		$def[$casecount] .= rrd::gprint ("case$casecount", "AVERAGE", "%3.2lf $UNIT[$casecount] AVERAGE \j");
		# TICKS ---------------------------------------------------------------------
		foreach ($this->DS as $k=>$v) {
			if(preg_match('/^c_'.$casecount.'__state/', $v['LABEL'], $state_matches)) {
				$def[$casecount] .= rrd::def("case".$casecount."_state", $v['RRDFILE'], $v['DS'], "MAX") ;
				$def[$casecount] .= rrd::cdef("case".$casecount."_state_unknown", "case".$casecount."_state,2,GT,case".$casecount."_state,0,IF") ;
				$def[$casecount] .= rrd::cdef("case".$casecount."_state_nok", "case".$casecount."_state,0,GT,case".$casecount."_state,0,IF") ;
				$def[$casecount] .= rrd::cdef("case".$casecount."_state_nok2", "case".$casecount."_state_nok,3,LT,case".$casecount."_state_nok,0,IF") ;
				$def[$casecount] .= "TICK:case".$casecount."_state_nok2".$col_NOK.$ticker_opacity.":".$ticker_frac.":not_ok " ;
				$def[$casecount] .= "TICK:case".$casecount."_state_unknown".$col_UNKN.$unkn_tick_opacity.":".$unkn_tick_frac.": " ;
			}
		}
		$def[$casecount] .= "TICK:case".$casecount."_unknown".$col_UNKN.$unkn_tick_opacity.":".$unkn_tick_frac.":unknown/stale " ;
		$def[$casecount] .= "VRULE:".$NAGIOS_TIMET."#000000:\"Last Service Check \\n\" ";

                if ($graph_cpu or $graph_mem) {
                        $def[$casecount] .= rrd::comment(" \\n");
                        $def[$casecount] .= rrd::comment("Host Statistics\:\\n");
                        $opt[$casecount] .= " --right-axis \"1:0\" --right-axis-label \"%\" ";
                }
		if ( $graph_cpu ) {
			$def[$casecount] .= $rrddef_cpu;	
		}
 		if ( $graph_mem ) {
			$def[$casecount] .= $rrddef_mem;	
		}

	}
}


if ( $DEBUG == 1 ) {
#throw new Kohana_exception(print_r($def,TRUE));
#throw new Kohana_exception(print_r($idxm1,TRUE));
}

