#!/usr/bin/env sh

cd `dirname $0`
cd ..

Usage() {
    echo -e "   Usage: $(basename $0) command [options]
    command:
       upload: 把config目录下的所有配置，上传到config server中
       register: 往config server中注册当前的应用
       load: 从config server中加载配置，替换到config目录中
    options:
       -Denv=dev 要加载配置的环境，默认为dev pro-生产环境; dev-开发环境
       -Dpassword=password 定义config server的密码
       -Dhost=host 定义config server的地址
    例： 
    $(basename $0) upload
    $(basename $0) load -Denv=dev -Dtoken=xxx
    $(basename $0) load -Denv=pro -Dtoken=xxx
    $(basename $0) register
        "
}

p1=$1;
p2=$2;
p3=$3;
p4=$4;
p5=$5;

EXEC_MVN() {
    mvn $1 $p2 $p3 $p4
}

case "$p1" in 
    "")                      Usage            ;;
    "-h")                    Usage            ;;
    upload)         EXEC_MVN config:upload;;
    register)         EXEC_MVN config:register;;
    load)         EXEC_MVN config:load;;
esac
