all:

loc:
	wc -l \
	*/*/*java \
	*/*/*/*java \
	*/*/*/*/*java

jars: 
	cd ../bin; jar cfvm ../facsanadu.jar ../src/MANIFEST.STARTQTCLIENT \
	*/*/*.class \
	*/*/*/*.class \
	*/*/*/*/*.class \
	*/*/*.png

############################################################################################
############################ osx ###########################################################
############################################################################################

macport: jars
	rm -Rf ../release/mac
	cp -r ../ports/mac ../release/mac
	cp ../facsanadu.jar ../lib/*jar ../lib/mac/*.jar ../release/mac/facsanadu.app/
#	cp translations/* ../release/mac/labstory.app/translations/
	cd ../release/mac/; make


############################################################################################
############################ linux #########################################################
############################################################################################

ubuntuport: jars
	sudo rm -f ../release/facsanadu*deb
	sudo java -cp bin ports.ubuntu.Main
	sudo mv ../release/facsanadu*deb ../release/facsanadu.deb

linuxport32: jars
	mkdir -p ../release/linux32
	rm -Rf ../release/linux32/labstory
	cp -r ports/linux ../release/linux32/facsanadu
	cp -r R facsanadu.jar labstoryPostload.jar lib/*jar lib/linux32/*jar ../release/linux32/facsanadu/
	cp translations/* ../release/linux32/facsanadu/translations/

linuxport64: jars
	mkdir -p ../release/linux64
	rm -Rf ../release/linux64/facsanadu
	cp -r ports/linux ../release/linux64/facsanadu
	cp -r R facsanadu.jar labstoryPostload.jar lib/*jar lib/linux64/*jar ../release/linux64/facsanadu/
	cp -r translations/* ../release/linux64/labstory/translations/

linuxport: linuxport32 linuxport64 ubuntuport



############################################################################################
############################ windows #######################################################
############################################################################################

winportjre: jars
	rm -Rf ../release/winjre/
	mkdir -p ../release/winjre/facsanadu
	cp ports/win/starter/facsanadu_bundledjre.exe ../release/winjre/facsanadu/facsanadu.exe
	cp ports/win/installer/* ../release/winjre/
	cp facsanadu.jar lib/*jar lib/win32/*jar ../release/winjre/facsanadu/
	cp -r R ../release/winjre/facsanadu/
	cp -r translations ../release/winjre/facsanadu/
	cp -r ../support_programs/jre ../release/winjre/
	cd ../release/winjre; "/home/mahogny/.wine/drive_c/Program Files (x86)/NSIS/makensis.exe" bundledjre.nsi
	cd ../release/winjre; mv install.exe ../facsanadu_winjre.exe


winport: winportjre # winport32 winport64 winportjre


############################################################################################
############################ release and upload ############################################
############################################################################################

release: macport linuxport winport #docs
	mkdir -p ../release
	rm -f ../release/facsanadu*zip 
	cd ../release/linux64; zip -r ../facsanadu_linux64.zip  facsanadu
	cd ../release/linux32; zip -r ../facsanadu_linux32.zip  facsanadu
	cd ports; zip -r ../../release/docs.zip            documentation
	cp -f org/ecobima/labnote/qtclient/version.txt ../release/qtversion.txt

cleanports:
	cd ports; rm facsanadu.app/*.jar
#	cd ports; rm linux/*.jar
#	cd ports; rm win32/*.jar
	cd ports; rm documentation/*


upload:
	cd ../release; scp *.dmg *.zip *.deb facsanadu_winjre.exe qtversion.txt mahogny@www.endrov.net:/home/mahogny/hp/other

uploadwin:
	cd ../release; scp *.exe 177655_mahogny@ssh.binero.se:/storage/content/55/177655/labstory.se/public_html/release/


timestamp:
	date +%s > org/ecobima/labnote/qtclient/timestamp.txt

uploadbackup:
	cd ../..; scp -r labnote3.git 177655_mahogny@ssh.binero.se:/storage/content/55/177655/



############################################################################################
############################ misc ##########################################################
############################################################################################

lupdate:
	lupdate -no-obsolete -extensions java \
	*/*java \
	org/ecobima/labnote/*/*java \
	org/ecobima/labnote/*/*/*java \
	org/ecobima/labnote/*/*/*/*java \
	org/ecobima/labnote/*/*/*/*/*java \
	-ts translations/*ts





gitaddall:
	git add --all \
	*/*java \
	*/*/*java \
	*/*/*/*java \
	*/*/*png

