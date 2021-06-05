#!/bin/bash
#echo 备份a.jar 请备份为a.jar.***  *** 为你自定义字符串
echo 初次使用请读本脚本第二行注释.jar,nohup及JVM方式启动请参阅脚本注释部分自行选择
#请检查如下配置是否正确，且在备份jar文件最好为 *.jar.bk时间戳
echo --------------------------[重]启动  "[`date +%Y-%m-%d\ %H:%M:%S`]"--------------------------
rm -f jarname.tmp
rm -f nohub.log

ls | grep *.jar  | grep -v *.jar.* > jarname.tmp
_JAR_NAME=`cat jarname.tmp | awk '{print $1}'`
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
echo $PID > pid.tmp

#define flag , only Y will restart
YES_OR_NO='Y'
if [ -z "$PID" ]
then
    echo -------------------------- $_JAR_NAME 服务已经关闭
else
	#check if will kill and restart
	echo -n  "$_JAR_NAME 服务已开启,是否需要重启?:Y(重启)/N(否) >"
	read  YES_OR_NO
	
	if [[ $YES_OR_NO = 'Y' ]]; then
		echo "-------------------------- 输入 $YES_OR_NO, 现在执行重启中 .... "
		echo -------------------------- kill -9 $PID 
	    kill -9 $PID
	    echo -------------------------- killed $PID $_JAR_NAME 服务,请检查.
		sleep 1s
	else
		echo "$YES_OR_NO 不重启"
	fi
fi

sleep 1s

if [[ $YES_OR_NO = 'Y' ]]; then
	CURRENT_VERSION=$(head -1 VERSION)
	echo --------------------------当前版本 $CURRENT_VERSION
	echo --------------------------执行启动
	
#	java -jar $_JAR_NAME -Dversion=$CURRENT_VERSION -Dabsolutepath=$CURRENT_PATH &
#	java -Xms2048m -Xmx4096m -Xss1024K -XX:PermSize=256m -XX:MaxPermSize=512m -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails -Xloggc:logs/gc_$DATE.log -XX:+PrintGCTimeStamps  -jar $_JAR_NAME -Dversion=$CURRENT_VERSION -Dabsolutepath=$CURRENT_PATH &
	nohup java -jar $_JAR_NAME -Dversion=$CURRENT_VERSION -Dabsolutepath=$CURRENT_PATH > nohub.log 2>&1 &

	sleep 1s
	
	echo --------------------------检查启动后结果 $_JAR_NAME.
	ps -ef | grep  --color=auto $_JAR_NAME | grep -v grep
	
else
	echo "..."
fi

echo --------------------------[重]启动完毕,请自行检查服务  "[`date +%Y-%m-%d\ %H:%M:%S`]"--------------------------