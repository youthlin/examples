package main

import (
	"errors"
	"fmt"
	"io"
	"math/rand"
	"os"
	"time"

	"github.com/urfave/cli/v2"
	"github.com/youthlin/t"
)

func main() {
	Init()
	app := &cli.App{
		Name:   t.T("Guess Numer"),
		Usage:  t.T("A cli demo about i18n(use github.com/youthlin/t)"),
		Action: run,
	}
	app.Run(os.Args)
}

func Init() {
	rand.Seed(time.Now().UnixNano())

	path := os.Getenv("LANG_PATH")
	if path == "" {
		path = "./lang"
	}
	t.BindDefaultDomain(path)
	t.SetLocale("")
}

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
