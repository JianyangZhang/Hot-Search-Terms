#!/bin/bash

echo ""

echo -e "\nbuild docker hadoop image\n"
sudo docker build -t joway/hadoop-cluster:latest .

echo ""
