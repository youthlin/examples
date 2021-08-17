package main

import (
	"errors"
	"fmt"
	"io"
	"math/rand"
	"os"
	"path/filepath"
	"strings"
	"time"

	"github.com/urfave/cli/v2"
	"github.com/youthlin/t"
	"golang.org/x/text/language"
	"golang.org/x/text/language/display"
)

// Init init rand seed and language
func Init() {
	rand.Seed(time.Now().UnixNano())

	path := os.Getenv("LANG_PATH")
	if path == "" {
		path = "./lang"
	}
	t.BindDefaultDomain(path) // load po/mo files
	t.SetLocale("")           // use system language
}

// main app entrance
func main() {
	Init()
	app := &cli.App{
		Flags: []cli.Flag{&cli.StringFlag{
			Name:    "lang",
			Aliases: []string{"l"},
		}},
		Before: setUserLang,
		Commands: []*cli.Command{{
			Name:    "show-langs",
			Aliases: []string{"show"},
			Action:  showLangs,
		}, {
			Name:    "help",
			Aliases: []string{"h"},
			Action: func(c *cli.Context) error {
				setAppOutputs(c.App)
				args := c.Args()
				if args.Present() {
					return cli.ShowCommandHelp(c, args.First())
				}
				_ = cli.ShowAppHelp(c)
				return nil
			},
			HideHelp: true,
		}},
		Action: run,
		OnUsageError: func(c *cli.Context, err error, isSubcommand bool) error {
			_, _ = fmt.Fprintf(c.App.ErrWriter, "%s %s\n\n", t.T("Incorrect Usage."), err.Error())
			_ = cli.ShowAppHelp(c)
			return nil
		},
	}
	setAppOutputs(app)
	app.Run(os.Args)
}

// setAppOutputs for case: app -l en help
func setAppOutputs(app *cli.App) {
	appName := filepath.Base(os.Args[0])
	app.Name = t.T("Guess Numer")
	app.Usage = t.T("A cli demo about i18n(use github.com/youthlin/t)")
	app.UsageText = t.T("%v [global options] command [command options] [arguments...]", appName)
	app.CustomAppHelpTemplate = replace(
		cli.AppHelpTemplate,
		t.Noop.T("NAME:"),
		t.Noop.T("USAGE:"),
		t.Noop.T("VERSION:"),
		t.Noop.T("DESCRIPTION:"),
		t.Noop.T("AUTHOR"),
		t.Noop.T("COMMANDS:"),
		t.Noop.T("GLOBAL OPTIONS:"),
		t.Noop.T("COPYRIGHT:"),
	)
	commandHelpTmpl := replace(
		cli.CommandHelpTemplate,
		t.Noop.T("NAME:"),
		t.Noop.T("USAGE:"),
		t.Noop.T("CATEGORY:"),
		t.Noop.T("DESCRIPTION:"),
		t.Noop.T("OPTIONS:"),
	)
	app.Flags[0].(*cli.StringFlag).Usage = t.T("language to use")
	app.Commands[0].Usage = t.T("list all loaded languages")
	app.Commands[0].ArgsUsage = t.T("[arguments...]")
	app.Commands[0].CustomHelpTemplate = commandHelpTmpl
	app.Commands[1].Usage = t.T("Shows a list of commands or help for one command")
	app.Commands[1].UsageText = t.X("usage of `help` command", "%v help [command options] [command]", appName)
	app.Commands[1].ArgsUsage = t.T("[command]")
	app.Commands[1].CustomHelpTemplate = commandHelpTmpl
}

func replace(input string, search ...string) string {
	for _, item := range search {
		input = strings.ReplaceAll(input, item, t.T(item))
	}
	return input
}

// setUserLang set user prefer language from app flag
func setUserLang(c *cli.Context) error {
	lang := c.String("lang")
	t.SetLocale(lang)
	return nil
}

// run start a game.
func run(c *cli.Context) error {
	var (
		guess  int
		min    = 0
		max    = 99
		step   = 0
		secret = rand.Intn(max + 1)
	)
	for {
		step++
		fmt.Print(t.T("Input your guess[%02d-%02d]> ", min, max))
		if _, err := fmt.Scan(&guess); err != nil {
			if errors.Is(err, io.EOF) {
				return nil
			}
			fmt.Println(t.T("Please input a number between %[1]d and %[2]d. (error message: %v)", min, max, err))
			continue
		}
		switch {
		case guess < secret:
			fmt.Println(t.T("Too short!"))
			if guess > min {
				min = guess
			}
		case guess == secret:
			fmt.Println(t.N(
				// TRANSLATORS: 1=the secret number; 2=steps
				"You got that: %[1]d! (used only one step!)", // sigular
				"You got that: %[1]d! (used %[2]d steps.)",   // plural
				step,         // n
				secret, step, // args
			))
			return nil
		case guess > secret:
			fmt.Println(t.T("Too large!"))
			if guess < max {
				max = guess
			}
		}
	}
}

// showLangs is a sub-command, shows all supported languages.
func showLangs(c *cli.Context) error {
	supports := t.SupportLangs(t.DefaultDomain)
	count := len(supports)
	fmt.Println(t.N("One language supported.", "%d languages supported.", count, count))
	for _, lang := range supports {
		tag := language.Make(lang)
		fmt.Printf("\t- %[1]v (%[2]v)\n", lang, display.Self.Name(tag))
	}
	return nil
}
