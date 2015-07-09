export PATH=${PATH}:/Users/baidu/Android/adt-bundle-mac-x86_64-20140702/sdk/platform-tools
export PATH=${PATH}:/Users/baidu/Android/adt-bundle-mac-x86_64-20140702/sdk/tools
export ANDROID_HOME=/Users/baidu/Android/adt-bundle-mac-x86_64-20140702/sdk
export ANT_HOME=/usr/local/apache-ant-1.9.4
export NDK_HOME=/Users/baidu/Android/android-ndk-r10d
export PATH=${PATH}:${ANT_HOME}/bin
export PATH=${PATH}:/Users/baidu/Android/android-ndk-r10d
export PS1='[\u @ \w] $ '
source ~/.git-completion.bash
alias ll='ls -alF'
alias la='ls -A'
alias l='ls -CF'

export ANDROID_NDK=/Users/baidu/Android/android-ndk-r10d
export CC=${ANDROID_NDK}/toolchains/arm-linux-androideabi-4.6/prebuilt/darwin-x86_64/bin/arm-linux-androideabi-gcc
export CCOPT="-O2 -fpic --sysroot=/home/ducalpha/adr/ndk/platforms/android-14/arch-arm -DANDROID -DOS_ANDROID"
export CFLAGS="--sysroot=${ANDROID_NDK}/platforms/android-14/arch-arm -DANDROID -DOS_ANDROID"
export LDFLAGS="--sysroot=${ANDROID_NDK}/platforms/android-14/arch-arm -fPIC -mandroid -L${ANDROID_NDK}/platforms/android-14/arch-arm/usr/lib"
