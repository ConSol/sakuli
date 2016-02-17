package helper

import (
	"os"
	"runtime"
	"strings"
)

//IsRunningOnWindows returns true if the program is running on Windows
func IsRunningOnWindows() bool {
	return runtime.GOOS == "windows"
}

//IsRunningOnLinux returns true if the program is running on Linux
func IsRunningOnLinux() bool {
	return runtime.GOOS == "linux"
}

//GenClassPath concatenates the elements with ; on Windows with : on Linux
func GenClassPath(elements ...string) string {
	if IsRunningOnWindows() {
		return strings.Join(elements, ";")
	} else if IsRunningOnLinux() {
		return strings.Join(elements, ":")
	} else {
		panic("Can not detect operatingsystem. Supported are: Windows, Linux")
	}
}

//DoesFileExist returns true if the given file exists
func DoesFileExist(file string) bool {
	_, err := os.Stat(file)
	return err == nil
}
