#!/bin/bash
#echo 备份a.jar 请备份为a.jar.***  *** 为你自定义字符串
echo 初次使用请读本脚本第二行注释
#请检查如下配置是否正确，且在备份jar文件最好为 *.jar.bk时间戳
echo --------------------------关闭  "[`date +%Y-%m-%d\ %H:%M:%S`]"--------------------------
rm -f jarname.tmp
ls | grep *.jar  | grep -v *.jar.* > jarname.tmp
_JAR_NAME=`cat jarname.tmp | awk '{print $1}'`
#_JAR_NAME=ots-cloud-v1.0.1.jar
CURRENT_PATH=$(pwd)
echo 当前jar名字：$_JAR_NAME ,路径: $CURRENT_PATH -- 如果不对，请检查本脚本

echo --------------------------检查当前的本jar服务名--------------------------
sleep 1s
ps -ef | grep  --color=auto $_JAR_NAME | grep -v grep 
sleep 1s

echo --------------------------检查当前路径的本jar服务名--------------------------
ps -ef | grep  $_JAR_NAME | grep  --color=auto $CURRENT_PATH | grep -v grep 

echo --------------------------

PID=$(ps -ef | grep $_JAR_NAME | grep $CURRENT_PATH | grep -v grep | awk '{ print $2 }')
echo --------------------------当前PID为 : $PID

if [ -z "$PID" ]
then
    echo -------------------------- $_JAR_NAME 服务已经关闭
else
	echo "-------------------------- 现在执行关闭服务中 .... "
	echo -------------------------- kill -9 $PID
    kill -9 $PID
    echo -------------------------- killed $PID $_JAR_NAME 服务,请检查.
	sleep 1s
fi
 
echo --------------------------关闭完毕,请自行检查服务  "[`date +%Y-%m-%d\ %H:%M:%S`]"--------------------------