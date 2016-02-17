package execute

import (
	"os"
	"os/exec"
	"syscall"
)

//Execute calls the given command with args. It returns the returncode, stdout,stderr, error
func Execute(command string, args ...string) (int, error) {
	cmd := exec.Command(command, args...)
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	var waitStatus syscall.WaitStatus
	returnCode := -1
	err := cmd.Run()
	if err != nil {
		if exitError, ok := err.(*exec.ExitError); ok {
			waitStatus = exitError.Sys().(syscall.WaitStatus)
			returnCode = waitStatus.ExitStatus()
		}
	} else {
		waitStatus = cmd.ProcessState.Sys().(syscall.WaitStatus)
		returnCode = waitStatus.ExitStatus()
	}
	return returnCode, err
}
