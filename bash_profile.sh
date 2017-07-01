export JAVA_7_HOME=`/usr/libexec/java_home -v 1.7`
export JAVA_8_HOME=`/usr/libexec/java_home -v 1.8`

alias jdk7="export JAVA_HOME=$JAVA_7_HOME"
alias jdk8="export JAVA_HOME=$JAVA_8_HOME"
#export JAVA_HOME=$JAVA_7_HOME
export JAVA_HOME=$JAVA_8_HOME
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar


export ANDROID_HOME=/Users/wayne/Android/sdk
export PATH=${PATH}:/Users/Wayne/Android/sdk/platform-tools
export PATH=${PATH}:/Users/Wayne/Android/sdk/tools
export PATH=${PATH}:/Users/Wayne/Android/sdk/build-tools
export android_home=$ANDROID_HOME
export proguard_home=$android_home/tools/proguard


export ANT_HOME=/Users/wayne/apache-ant-1.9.9
export PATH=${PATH}:${ANT_HOME}/bin
ANT_OPTS=-Xmx2g


#export PS1='[\u @ \w] $ '
#source ~/.git-completion.bash


alias ll='ls -alF'
alias la='ls -A'
alias l='ls -CF'
alias grep='grep --color'
