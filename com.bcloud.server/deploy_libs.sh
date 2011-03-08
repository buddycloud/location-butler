#!/bin/bash
set -x # show us what commands are running

#---------------------------------------------
# This is the deploy script for butler dependencies (.jars)
#---------------------------------------------

user=$1
component=$2
server=$3

#---------------------------------------------
# check that args are provided
#---------------------------------------------
if [ -z $user ]; then 
	echo "Usage:   deploy_dependencies [remote user] [component] [server]"
	echo "Example: deploy_dependencies simon locationbutler cave.buddycloud.com"
	exit
fi

if [ -z $component ]; then 
	echo "Usage:   deploy_dependencies [remote user] [component] [server]"
	echo "Example: deploy_dependencies simon locationbutler cave.buddycloud.com"
	exit
fi

if [ -z $server ]; then 
	echo "Usage:   deploy_dependencies [remote user] [component] [server]"
	echo "Example: deploy_dependencies simon locationbutler cave.buddycloud.com"
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
# make sure lib dir is up to date
#---------------------------------------------
rsync -e ssh -r lib $user@$server:/opt/buddycloud-$component/

