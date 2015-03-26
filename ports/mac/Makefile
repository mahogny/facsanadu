all:
	rm -Rf foo
	mkdir foo
	cp -r quickfacs.app foo/
	genisoimage -o ../quickfacs_osx.dmg -V QuickFACS -r -apple foo
