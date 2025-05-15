#!/bin/bash

SCRIPT_DIR=$(dirname "${BASH_SOURCE[0]}")
DEBS_PATH="/usr/local/share"

# Create DEBIAN folder and control file
mkdir -p $SCRIPT_DIR/../Distribution/Solvinery/DEBIAN &&

echo -e "Package: Solvinery
Version: 1.0
Architecture: all
Maintainer: Your Name <you@example.com>
Description: Installs two .deb packages" > $SCRIPT_DIR/../Distribution/Solvinery/DEBIAN/control &&

# Create postinst script
echo -e "#!/bin/sh\n
dpkg -i $DEBS_PATH/solvinery-front_0.1.0_amd64.deb\n
dpkg -i $DEBS_PATH/scipoptsuite.deb\n
\n" > $SCRIPT_DIR/../Distribution/Solvinery/DEBIAN/postinst &&

chmod +x $SCRIPT_DIR/../Distribution/Solvinery/DEBIAN/postinst &&

# Create the necessary folder to store .deb files
mkdir -p $SCRIPT_DIR/../Distribution/Solvinery/usr/local/share &&

# Copy the .deb files to the shared folder
cp $SCRIPT_DIR/../Distribution/Solvinery-front_0.1.0_amd64.deb $SCRIPT_DIR/../Distribution/linux-unpacked/resources/installers/linux/scipoptsuite.deb $SCRIPT_DIR/../Distribution/Solvinery/usr/local/share/ &&

# Build the .deb package
dpkg-deb --build $SCRIPT_DIR/../Distribution/Solvinery

