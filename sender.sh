#!/bin/bash

if [ ! "$#" -eq 3 ] ; then

  echo "usage: $0 IP_ADDRESS PORT NUMBER_TO_SEND"
  exit 1

fi

if [[ ! "$1" =~ ^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$ ]] ; then

  echo "IP_ADDRESS argument must be a valid IPv4 address!"
  exit 2

fi

if [[ ! "$2" =~ ^[0-9]+$ ]] ; then

    echo "PORT argument must be positive integer!"
    exit 3

fi

if [[ ! "$3" =~ ^-?[0-9]+$ ]] ; then

    echo "NUMBER_TO_SEND argument must be integer!"
    exit 4

fi


echo -n "$3" | nc -u -w1  "$1" "$2"

exit 0
