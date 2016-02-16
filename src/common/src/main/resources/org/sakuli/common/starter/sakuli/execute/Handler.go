package execute

import (
	"fmt"
	"github.com/ConSol/sakuli/src/common/src/main/resources/org/sakuli/common/starter/sakuli/helper"
)

//RunHandler runs external program with no parameters
func RunHandler(executable string) {
	fmt.Printf("Calling Handler executable: %s\n", executable)
	if helper.DoesFileExist(executable) {
		returnCode, err := Execute(executable)
		if err != nil {
			fmt.Printf("Error while calling: %s\n", executable)
		}
		fmt.Printf("Handler [%s] finished with returncode: %d\n", executable, returnCode)
	} else {
		fmt.Printf("Can't find the file: %s -> will skip this hook\n", executable)
	}
}
