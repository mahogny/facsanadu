all:
	javac -Xlint:deprecation -Xdiags:verbose -d ../bin \
	-cp ../lib/jdom2-2.0.6.1.jar:../lib/qtjambi-6.4.1.jar:../lib/linux64/qtjambi-native-linux64-6.4.1.jar:../lib/vecmath-1.5.2.jar \
	*/*/*java \
        */*/*/*java \
        */*/*/*/*java
	cp facsanadu/gui/colors/commoncolors.txt ../bin/facsanadu/gui/colors/
	cp facsanadu/gui/resource/* ../bin/facsanadu/gui/resource/

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
	*/*/*/*.txt \
	*/*/*/*.png

clean:
	rm -rf ../bin/facsanadu
	rm -rf ../bin/port
	rm ../facsanadu.jar
	rm -rf ../release

############################################################################################
############################ osx ###########################################################
############################################################################################

macport: all jars
	rm -Rf ../release/mac
	cp -r ../ports/mac ../release/mac
	cp ../facsanadu.jar ../lib/*jar ../lib/mac/*.jar ../release/mac/FACSanadu.app/
#	cp translations/* ../release/mac/facsanadu.app/translations/
	cd ../release/mac/; make


############################################################################################
############################ linux #########################################################
############################################################################################

ubuntuport: all jars
	sudo rm -f ../release/facsanadu*deb
	cd ..; sudo java -cp bin port.ubuntu.Main
	sudo mv ../release/facsanadu*deb ../release/facsanadu.deb


linuxport64: all jars
	mkdir -p ../release/linux64
	rm -Rf ../release/linux64/facsanadu
	cd ..; cp -r ports/linux release/linux64/facsanadu
	cd ..; cp -r facsanadu.jar lib/*jar lib/linux64/*jar release/linux64/facsanadu/
	#cp -r translations/* ../release/linux64/facsanadu/translations/

linuxport: linuxport64 ubuntuport



############################################################################################
############################ windows #######################################################
############################################################################################

winportjre: all jars
	rm -Rf ../release/winjre/
	mkdir -p ../release/winjre/facsanadu
	cd ..; cp ports/win/starter/facsanadu_bundledjre.exe release/winjre/facsanadu/start.exe
	cd ..; cp ports/win/installer/* release/winjre/
	cd ..; cp facsanadu.jar lib/*jar lib/win64/*jar release/winjre/facsanadu/
	#cp -r translations ../release/winjre/facsanadu/
	cd ..; cp -r ../support_programs/jre release/winjre/
	cd ../release/winjre; "/home/mahogny/.wine/drive_c/Program Files (x86)/NSIS/makensis.exe" bundledjre.nsi
	cd ../release/winjre; mv install.exe ../facsanadu_winjre.exe


winportzip: all jars
	rm -Rf ../release/facsanadu_win/
	mkdir -p ../release/facsanadu_win
	cd ..; cp ports/win/starter/facsanadu_bundledjre.exe release/facsanadu_win/facsanadu.exe
	cd ..; cp facsanadu.jar lib/*jar lib/win64/*jar release/facsanadu_win/
	cd ..; cp -r ../support_programs/jre/* release/facsanadu_win/
	cd ../release/; zip -r facsanadu_win facsanadu_win



winport: winportzip # winportjre # winport64 winportjre


############################################################################################
############################ release and upload ############################################
############################################################################################

release: macport linuxport winport #docs
	mkdir -p ../release
	rm -f ../release/facsanadu*zip 
	cd ../release/linux64; zip -r ../facsanadu_linux64.zip  facsanadu
	#cd ports; zip -r ../../release/docs.zip            documentation
	cd ..; cp -f src/facsanadu/gui/version.txt release/qtversion.txt

cleanports:
	cd ports; rm facsanadu.app/*.jar
#	cd ports; rm linux/*.jar
#	cd ports; rm win64/*.jar
	cd ports; rm documentation/*


upload:
#	cd ../release; scp *.dmg *.zip *.deb facsanadu_winjre.exe qtversion.txt mahogny@www.endrov.net:/home/mahogny/hp/facsanadu
	cd ../release; scp *.dmg *.zip *.deb qtversion.txt 177655_mahogny@ssh.binero.se:/storage/content/55/177655/facsanadu.org/public_html/release/

uploadwin:
	cd ../release; scp *.exe 177655_mahogny@ssh.binero.se:/storage/content/55/177655/facsanadu.se/public_html/release/


timestamp:
	cd ..; date +%s > bin/facsanadu/gui/timestamp.txt

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

