#!/bin/bash

if [ ! "$#" -eq 1 ] ; then

  echo "usage: $0 PORT"
  exit 1
  
fi  

if [[ ! "$1" =~ ^[0-9]+$ ]] ; then

  echo "PORT argument must be positive integer!"
  exit 2
  
fi

nc -ulk $1

exit 0
