#!/bin/bash
set -x # show us what commands are running


#---------------------------------------------
# This is the deploy script that took waaaay to long to write....
#---------------------------------------------

user=$1
component=$2
server=$3

#---------------------------------------------
# check that args are provided
#---------------------------------------------
if [ -z $user ]; then 
	echo "Usage:   deploy [remote user] [component] [server]"
	echo "Example: deploy simon locationbutler cave.buddycloud.com"
	exit
fi

if [ -z $component ]; then 
	echo "Usage:   deploy [remote user] [component] [server]"
	echo "Example: deploy simon locationbutler cave.buddycloud.com"
	exit
fi

if [ -z $server ]; then 
	echo "Usage:   deploy [remote user] [component] [server]"
	echo "Example: deploy simon locationbutler cave.buddycloud.com"
	exit
fi

#---------------------------------------------
# A safeguard
#---------------------------------------------
live_server="cave.buddycloud.com"
if [ $server == $live_server ];
then
   echo "------------------------------------------"
   echo "WARNING! YOU ARE DEPLOYING TO LIVE SYSTEM!"
   echo "DO YOU WANT TO PROCEED (yes/no)?"
   echo "------------------------------------------"
   read continue
   echo $continue
   if [ ! $continue=="yes" ]; then 
      exit
   fi
fi

#---------------------------------------------
# get svn HEAD revision number
#---------------------------------------------
rev=`svn info -r HEAD | grep Revision | sed "s/Revision: //"`

#---------------------------------------------
# collect all artefacts into revision sub directory
echo "Creating deploy/$rev/"
mkdir deploy/$rev/
mkdir deploy/$rev/cron
cp deploy/*.jar deploy/$rev/
cp log4j.properties deploy/$rev/
cp cron.hourly.sh deploy/$rev/cron
cp cron.daily.sh deploy/$rev/cron
if [ $server == $live_server ] 
then
   cp config.xml deploy/$rev/config.xml
else
   cp config_beta.xml deploy/$rev/config.xml
fi

#---------------------------------------------
# write revision log to file
#---------------------------------------------
svn log -r HEAD > deploy/$rev/revision_log.txt

#---------------------------------------------
# make sure content is up to date
#---------------------------------------------
rsync -e ssh -r deploy/$rev $user@$server:/opt/buddycloud-$component/

#---------------------------------------------
# give group rights of all files to buddycloud-dev
# delete old 'current' symlink
# create 'current' symlink
# restart component
#---------------------------------------------
echo "Applying permissions, updating symlink, restarting $component..."
ssh $user@$server "find /opt/buddycloud-$component/$rev  -exec chgrp buddycloud-dev {} \;  ; \
	find /opt/buddycloud-$component/$rev -type d -exec chmod 770 {} \;  ; \
	find /opt/buddycloud-$component/$rev -type f -exec chmod 660 {} \;  ; \
	rm /opt/buddycloud-$component/current; \
	ln -s /opt/buddycloud-$component/$rev /opt/buddycloud-$component/current; \
	sudo /etc/init.d/buddycloud-$component restart"
