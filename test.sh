#!/usr/bin/env bash

max="$1"
date

get () {
  curl -s -w "\nhttp status: %{http_code}" --request GET "http://localhost:8080/greeting"; echo
}

for i in `seq 1 $max`
do
  echo "Request $i : "
  get
done
