VERSION=`./gradlew version | grep Version | cut -f 2 -d " "`
PASSPHRASE=`cat ~/.gnupg/passphrase.txt`
GPGPARAMS="--passphrase $PASSPHRASE --batch --yes --no-tty"
./gradlew assemble generatePom

gpg $GPGPARAMS -ab build/pom.xml
gpg $GPGPARAMS -ab build/libs/kllvm-${VERSION}.jar
gpg $GPGPARAMS -ab build/libs/kllvm-${VERSION}-javadoc.jar
gpg $GPGPARAMS -ab build/libs/kllvm-${VERSION}-sources.jar
cd build/libs
jar -cvf bundle-kllvm.jar ../pom.xml ../pom.xml.asc kllvm-${VERSION}.jar kllvm-${VERSION}.jar.asc kllvm-${VERSION}-javadoc.jar kllvm-${VERSION}-javadoc.jar.asc kllvm-${VERSION}-sources.jar kllvm-${VERSION}-sources.jar.asc
cd ../..

mkdir -p release
mv build/libs/bundle-kllvm.jar release
