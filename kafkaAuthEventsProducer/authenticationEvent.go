package main

type AuthenticationEvent struct {
	Username   string
	Ip         string
	Successful bool
	Timestamp  int64
}
