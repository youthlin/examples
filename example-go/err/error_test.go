package err_test

import (
	"fmt"
	"testing"

	std "errors"
	"github.com/cockroachdb/errors"
	pkg "github.com/pkg/errors"
)

func stdFmtError() error {
	return fmt.Errorf("format error")
}

func stdNew() error {
	return std.New("std/errors.New")
}

func pkgErr() error {
	return pkg.Errorf("pkg/errors")
}

func dbErr() error {
	return errors.Errorf("cockroachdb")
}

func pkgWrap(err error) error {
	return pkg.Wrapf(err, "wrap message")
}

func dbWrap(err error) error {
	return errors.Wrapf(err, "wraped by cockroachdb")
}

func printErr(t *testing.T, err error) {
	t.Logf("err=%+v\n------\n", err)
}
func TestCockroachdb(t *testing.T) {
	printErr(t, stdFmtError()) // err=format error
	printErr(t, stdNew())      // err=std/errors.New

	printErr(t, pkgErr())
	// err=pkg/errors
	// github.com/youthlin/examples/example-go/err_test.pkgErr
	// 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:21
	// github.com/youthlin/examples/example-go/err_test.TestCockroachdb
	// 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:43
	// testing.tRunner
	// 	/Users/youthlin.chen/.go/current/src/testing/testing.go:1193
	// runtime.goexit
	// 	/Users/youthlin.chen/.go/current/src/runtime/asm_amd64.s:1371

	printErr(t, dbErr())
	// err=cockroachdb
	// (1) attached stack trace
	//   -- stack trace:
	//   | github.com/youthlin/examples/example-go/err_test.dbErr
	//   | 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:25
	//   | github.com/youthlin/examples/example-go/err_test.TestCockroachdb
	//   | 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:54
	//   | testing.tRunner
	//   | 	/Users/youthlin.chen/.go/current/src/testing/testing.go:1193
	//   | runtime.goexit
	//   | 	/Users/youthlin.chen/.go/current/src/runtime/asm_amd64.s:1371
	// Wraps: (2) cockroachdb
	// Error types: (1) *withstack.withStack (2) *errutil.leafError

	printErr(t, pkgWrap(stdFmtError()))
	// err=format error
	// wrap message
	// github.com/youthlin/examples/example-go/err_test.pkgWrap
	// 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:29
	// github.com/youthlin/examples/example-go/err_test.TestCockroachdb
	// 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:69
	// testing.tRunner
	// 	/Users/youthlin.chen/.go/current/src/testing/testing.go:1193
	// runtime.goexit
	// 	/Users/youthlin.chen/.go/current/src/runtime/asm_amd64.s:1371
	printErr(t, pkgWrap(pkgErr()))
	// err=pkg/errors
	// github.com/youthlin/examples/example-go/err_test.pkgErr
	// 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:21
	// github.com/youthlin/examples/example-go/err_test.TestCockroachdb
	// 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:80
	// testing.tRunner
	// 	/Users/youthlin.chen/.go/current/src/testing/testing.go:1193
	// runtime.goexit
	// 	/Users/youthlin.chen/.go/current/src/runtime/asm_amd64.s:1371
	// wrap message
	// github.com/youthlin/examples/example-go/err_test.pkgWrap
	// 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:29
	// github.com/youthlin/examples/example-go/err_test.TestCockroachdb
	// 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:80
	// testing.tRunner
	// 	/Users/youthlin.chen/.go/current/src/testing/testing.go:1193
	// runtime.goexit
	// 	/Users/youthlin.chen/.go/current/src/runtime/asm_amd64.s:1371

	printErr(t, dbWrap(stdFmtError()))
	// err=wraped by cockroachdb: format error
	// (1) attached stack trace
	// 	 -- stack trace:
	// 	 | github.com/youthlin/examples/example-go/err_test.dbWrap
	// 	 | 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:33
	// 	 | github.com/youthlin/examples/example-go/err_test.TestCockroachdb
	// 	 | 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:100
	// 	 | testing.tRunner
	// 	 | 	/Users/youthlin.chen/.go/current/src/testing/testing.go:1193
	// 	 | runtime.goexit
	// 	 | 	/Users/youthlin.chen/.go/current/src/runtime/asm_amd64.s:1371
	// Wraps: (2) wraped by cockroachdb
	// Wraps: (3) format error
	// Error types: (1) *withstack.withStack (2) *errutil.withPrefix (3) *errors.errorString

	printErr(t, dbWrap(pkgErr()))
	// err=wraped by cockroachdb: pkg/errors
	// (1) attached stack trace
	//   -- stack trace:
	//   | github.com/youthlin/examples/example-go/err_test.dbWrap
	//   | 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:33
	//   | [...repeated from below...]
	// Wraps: (2) wraped by cockroachdb
	// Wraps: (3) pkg/errors
	//   | github.com/youthlin/examples/example-go/err_test.pkgErr
	//   | 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:21
	//   | github.com/youthlin/examples/example-go/err_test.TestCockroachdb
	//   | 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:116
	//   | testing.tRunner
	//   | 	/Users/youthlin.chen/.go/current/src/testing/testing.go:1193
	//   | runtime.goexit
	//   | 	/Users/youthlin.chen/.go/current/src/runtime/asm_amd64.s:1371
	// Error types: (1) *withstack.withStack (2) *errutil.withPrefix (3) *errors.fundamental

	printErr(t, pkgWrap(dbErr()))
	// err=cockroachdb
	// (1) attached stack trace
	//   -- stack trace:
	//   | github.com/youthlin/examples/example-go/err_test.dbErr
	//   | 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:25
	//   | github.com/youthlin/examples/example-go/err_test.TestCockroachdb
	//   | 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:135
	//   | testing.tRunner
	//   | 	/Users/youthlin.chen/.go/current/src/testing/testing.go:1193
	//   | runtime.goexit
	//   | 	/Users/youthlin.chen/.go/current/src/runtime/asm_amd64.s:1371
	// Wraps: (2) cockroachdb
	// Error types: (1) *withstack.withStack (2) *errutil.leafError
	// wrap message
	// github.com/youthlin/examples/example-go/err_test.pkgWrap
	// 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:29
	// github.com/youthlin/examples/example-go/err_test.TestCockroachdb
	// 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:135
	// testing.tRunner
	// 	/Users/youthlin.chen/.go/current/src/testing/testing.go:1193
	// runtime.goexit
	// 	/Users/youthlin.chen/.go/current/src/runtime/asm_amd64.s:1371

	printErr(t, dbWrap(dbErr()))
	// err=wraped by cockroachdb: cockroachdb
	// (1) attached stack trace
	//   -- stack trace:
	//   | github.com/youthlin/examples/example-go/err_test.dbWrap
	//   | 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:33
	//   | [...repeated from below...]
	// Wraps: (2) wraped by cockroachdb
	// Wraps: (3) attached stack trace
	//   -- stack trace:
	//   | github.com/youthlin/examples/example-go/err_test.dbErr
	//   | 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:25
	//   | github.com/youthlin/examples/example-go/err_test.TestCockroachdb
	//   | 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:159
	//   | testing.tRunner
	//   | 	/Users/youthlin.chen/.go/current/src/testing/testing.go:1193
	//   | runtime.goexit
	//   | 	/Users/youthlin.chen/.go/current/src/runtime/asm_amd64.s:1371
	// Wraps: (4) cockroachdb
	// Error types: (1) *withstack.withStack (2) *errutil.withPrefix (3) *withstack.withStack (4) *errutil.leafError

	printErr(t, dbWrap(dbWrap(dbErr())))
	// err=wraped by cockroachdb: wraped by cockroachdb: cockroachdb
	// (1) attached stack trace
	//   -- stack trace:
	//   | github.com/youthlin/examples/example-go/err_test.dbWrap
	//   | 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:33
	//   | github.com/youthlin/examples/example-go/err_test.TestCockroachdb
	//   | 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:180
	//   | testing.tRunner
	//   | 	/Users/youthlin.chen/.go/current/src/testing/testing.go:1193
	// Wraps: (2) wraped by cockroachdb
	// Wraps: (3) attached stack trace
	//   -- stack trace:
	//   | github.com/youthlin/examples/example-go/err_test.dbWrap
	//   | 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:33
	//   | [...repeated from below...]
	// Wraps: (4) wraped by cockroachdb
	// Wraps: (5) attached stack trace
	//   -- stack trace:
	//   | github.com/youthlin/examples/example-go/err_test.dbErr
	//   | 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:25
	//   | github.com/youthlin/examples/example-go/err_test.TestCockroachdb
	//   | 	/Users/youthlin.chen/go/src/github.com/youthlin/examples/example-go/err/error_test.go:180
	//   | testing.tRunner
	//   | 	/Users/youthlin.chen/.go/current/src/testing/testing.go:1193
	//   | runtime.goexit
	//   | 	/Users/youthlin.chen/.go/current/src/runtime/asm_amd64.s:1371
	// Wraps: (6) cockroachdb
	// Error types: (1) *withstack.withStack (2) *errutil.withPrefix (3) *withstack.withStack (4) *errutil.withPrefix (5) *withstack.withStack (6) *errutil.leafError
	// ------
}
