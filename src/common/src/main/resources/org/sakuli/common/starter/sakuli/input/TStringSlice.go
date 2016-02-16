package input

import (
	"fmt"
)

//StringSlice is used for multiple string args
type StringSlice []string

//String is for the flag.Var function
func (s *StringSlice) String() string {
	return fmt.Sprintf("%s", *s)
}

//Set is for the flag.Var function
func (s *StringSlice) Set(value string) error {
	*s = append(*s, value)
	return nil
}

//AddPrefix adds a given Prefix to every element
func (s StringSlice) AddPrefix(prefix string) StringSlice {
	localCopy := StringSlice{}
	for _, v := range s {
		localCopy = append(localCopy, prefix+v)
	}
	return localCopy
}
