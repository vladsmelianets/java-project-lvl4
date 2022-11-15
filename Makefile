.DEFAULT_GOAL := build-run

setup:
	make -C app setup

clean:
	make -C app clean

build:
	make -C app build

install:
	make -C app install

run:
	make -C app run

test:
	make -C app test

report:
	make -C app report

lint:
	make -C app lint

gen-migrations:
	make -C app gen-migrations

start:
	make -C app start

start-dist:
	make -C app start-dist

build-run:
	make -C app build run

check-updates:
	make -C app check-updates

.PHONY: build
