package input

import (
	"flag"
	"fmt"
	"github.com/ConSol/sakuli/src/common/src/main/resources/org/sakuli/common/starter/sakuli/helper"
	"path/filepath"
)

//TestRun makes validation tests on the run parameter
func TestRun(suite string) {
	if suite == "" {
		flag.Usage()
		panic("run param is empty")
	} else if !helper.DoesFileExist(suite) {
		panic(fmt.Sprintf("run parameter folder [%s] does not exitst\n", suite))
	}
}

//TestJavaHome returns a string if the javahome is valid, an empty if not
func TestJavaHome(home string) string {
	javaExecutable := "java"
	if home == "" {
		return javaExecutable
	}
	if helper.IsRunningOnWindows() {
		javaExecutable = filepath.Join(home, "java.exe")
	} else if helper.IsRunningOnLinux() {
		javaExecutable = filepath.Join(home, "java")
	} else {
		panic("Can not detect operatingsystem. Supported are: Windows, Linux")
	}

	if helper.DoesFileExist(home) {
		return javaExecutable
	}
	return ""
}
