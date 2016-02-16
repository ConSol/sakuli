package main

import (
	"flag"
	"fmt"
	"github.com/ConSol/sakuli/src/common/src/main/resources/org/sakuli/common/starter/sakuli/execute"
	"github.com/ConSol/sakuli/src/common/src/main/resources/org/sakuli/common/starter/sakuli/input"
	"os"
	"path/filepath"
	"time"
)

func main() {
	var loop int
	var javaHome string
	var javaProperties input.StringSlice
	var preHooks input.StringSlice
	var postHooks input.StringSlice
	var browser string
	var encrypt string
	var inter string
	var run string
	var sahiHome string
	var sakuliHome string

	if os.Getenv("SAKULI_HOME") == "" {
		panic("SAKULI_HOME environment variable is not set")
	}

	sakuliJars := filepath.Join(os.Getenv("SAKULI_HOME"), "libs", "java")
	originalUsage := flag.Usage
	flag.Usage = func() {
		fmt.Println(`Generic Sakuli test starter.
2016 - The Sakuli team / Philip Griesbacher.
`)
		originalUsage()
	}
	flag.IntVar(&loop, "loop", 0, "loop this suite, wait n seconds between executions, 0 means no loops (default: 0)")
	flag.StringVar(&javaHome, "javahome", "", "Java bin dir (overrides PATH)")
	flag.Var(&preHooks, "preHook", "A programm which will be executed before sakuli (Can be added multiple times)")
	flag.Var(&postHooks, "postHook", "A programm which will be executed after sakuli (Can be added multiple times)")

	flag.Var(&javaProperties, "D", "JVM option to set a property on runtime, overrides the 'sakuli.properties'")
	flag.StringVar(&browser, "browser", "", "(optional) browser for the test execution (default: Firefox)")
	flag.StringVar(&encrypt, "encrypt", "", "encrypt a secret")
	flag.StringVar(&inter, "interface", "", "(optional) network interface used for encryption")
	flag.StringVar(&run, "run", "", "run a sakuli test suite")
	flag.StringVar(&sahiHome, "sahi_home", "", "(optional) Sahi installation folder")
	flag.StringVar(&sakuliHome, "sakuli_home", os.Getenv("SAKULI_HOME"), "(optional) SAKULI_HOME folder, default: environment variable 'SAKULI_HOME'")
	flag.Parse()

	input.TestRun(run)

	javaExecutable := input.TestJavaHome(javaHome)
	javaProperties = javaProperties.AddPrefix("-D")
	sakuliProperties := map[string]string{"sakuli_home": sakuliHome}

	if browser != "" {
		sakuliProperties["browser"] = browser
	}
	if inter != "" {
		sakuliProperties["interface"] = inter
	}
	if encrypt != "" {
		sakuliProperties["encrypt"] = encrypt
	}
	if run != "" {
		sakuliProperties["run"] = run
	}
	if sahiHome != "" {
		sakuliProperties["sahiHome"] = sahiHome
	}
	joinedSakuliProperties := genSakuliPropertiesList(sakuliProperties)

	fmt.Println("=========== Starting Pre-Hooks ===========")
	for _, pre := range preHooks {
		execute.RunHandler(pre)
	}
	fmt.Println("=========== Finished Pre-Hooks ===========")

	sakuliReturnCode := execute.RunSakuli(javaExecutable, sakuliJars, javaProperties, joinedSakuliProperties)
	for loop > 0 {
		fmt.Printf("*** Loop mode - sleeping for %d seconds... ***\n", loop)
		time.Sleep(time.Duration(loop) * time.Second)
		execute.RunSakuli(javaExecutable, sakuliJars, javaProperties, joinedSakuliProperties)
	}

	fmt.Println("=========== Starting Post-Hooks ===========")
	for _, post := range postHooks {
		execute.RunHandler(post)
	}
	fmt.Println("=========== Finished Post-Hooks ===========")

	os.Exit(sakuliReturnCode)
}

func genSakuliPropertiesList(properties map[string]string) input.StringSlice {
	propertiesString := []string{}
	for k, v := range properties {
		propertiesString = append(propertiesString, fmt.Sprintf("--%s", k))
		propertiesString = append(propertiesString, v)
	}
	return propertiesString
}
