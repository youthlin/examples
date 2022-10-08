package main

import (
	"flag"
	"fmt"
	"log"
	"net/http"
	"path/filepath"
)

var (
	port int
	root string
)

func main() {
	parse()
	serve()
}

func parse() {
	flag.IntVar(&port, "port", 8088, "static file server port")
	flag.StringVar(&root, "path", ".", "root path")
	flag.Parse()
	abs, err := filepath.Abs(root)
	if err != nil {
		panic(err)
	}
	root = abs
	fmt.Printf("path=%v\nport=%v\n", root, port)
}

func serve() {
	var (
		addr = fmt.Sprintf(":%d", port)
		fs   = http.FileServer(http.Dir(root))
	)
	http.Handle("/", withLog(fs))
	http.ListenAndServe(addr, nil)
}

func withLog(handler http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		writer := NewResponseWriter(w)
		handler.ServeHTTP(writer, r)
		statusCode := writer.statusCode
		log.Printf("[%d %s]: %s %s ", statusCode, http.StatusText(statusCode), r.Method, r.URL)
	})
}

type ResponseWriter struct {
	// Capturing the HTTP status code from http.ResponseWriter
	// https://gist.github.com/Boerworz/b683e46ae0761056a636
	http.ResponseWriter
	statusCode int
}

func NewResponseWriter(w http.ResponseWriter) *ResponseWriter {
	return &ResponseWriter{ResponseWriter: w, statusCode: http.StatusOK}
}

func (w *ResponseWriter) WriteHeader(code int) {
	w.statusCode = code
	w.ResponseWriter.WriteHeader(code)
}
