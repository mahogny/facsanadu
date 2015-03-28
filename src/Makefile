all:

loc:
	wc -l \
	*/*/*java \
	*/*/*/*java \
	*/*/*/*/*java

jars: 
	cd ../bin; jar cfvm ../quickfacs.jar ../src/MANIFEST.STARTQTCLIENT \
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
	cp ../quickfacs.jar ../lib/*jar ../lib/mac/*.jar ../release/mac/quickfacs.app/
#	cp translations/* ../release/mac/labstory.app/translations/
	cd ../release/mac/; make


############################################################################################
############################ linux #########################################################
############################################################################################

ubuntuport: jars
	sudo rm -f ../release/quickfacs*deb
	sudo java -cp bin ports.ubuntu.Main
	sudo mv ../release/quickfacs*deb ../release/quickfacs.deb

linuxport32: jars
	mkdir -p ../release/linux32
	rm -Rf ../release/linux32/labstory
	cp -r ports/linux ../release/linux32/quickfacs
	cp -r R quickfacs.jar labstoryPostload.jar lib/*jar lib/linux32/*jar ../release/linux32/quickfacs/
	cp translations/* ../release/linux32/quickfacs/translations/

linuxport64: jars
	mkdir -p ../release/linux64
	rm -Rf ../release/linux64/quickfacs
	cp -r ports/linux ../release/linux64/quickfacs
	cp -r R quickfacs.jar labstoryPostload.jar lib/*jar lib/linux64/*jar ../release/linux64/quickfacs/
	cp -r translations/* ../release/linux64/labstory/translations/

linuxport: linuxport32 linuxport64 ubuntuport



############################################################################################
############################ windows #######################################################
############################################################################################

winportjre: jars
	rm -Rf ../release/winjre/
	mkdir -p ../release/winjre/quickfacs
	cp ports/win/starter/quickfacs_bundledjre.exe ../release/winjre/quickfacs/quickfacs.exe
	cp ports/win/installer/* ../release/winjre/
	cp quickfacs.jar lib/*jar lib/win32/*jar ../release/winjre/quickfacs/
	cp -r R ../release/winjre/quickfacs/
	cp -r translations ../release/winjre/quickfacs/
	cp -r ../support_programs/jre ../release/winjre/
	cd ../release/winjre; "/home/mahogny/.wine/drive_c/Program Files (x86)/NSIS/makensis.exe" bundledjre.nsi
	cd ../release/winjre; mv install.exe ../quickfacs_winjre.exe


winport: winportjre # winport32 winport64 winportjre


############################################################################################
############################ release and upload ############################################
############################################################################################

release: macport linuxport winport #docs
	mkdir -p ../release
	rm -f ../release/quickfacs*zip 
	cd ../release/linux64; zip -r ../quickfacs_linux64.zip  quickfacs
	cd ../release/linux32; zip -r ../quickfacs_linux32.zip  quickfacs
	cd ports; zip -r ../../release/docs.zip            documentation
	cp -f org/ecobima/labnote/qtclient/version.txt ../release/qtversion.txt

cleanports:
	cd ports; rm quickfacs.app/*.jar
#	cd ports; rm linux/*.jar
#	cd ports; rm win32/*.jar
	cd ports; rm documentation/*


upload:
	cd ../release; scp *.dmg *.zip *.deb quickfacs_winjre.exe qtversion.txt mahogny@www.endrov.net:/home/mahogny/hp/other

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

