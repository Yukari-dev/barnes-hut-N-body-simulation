#!/bin/sh


mvn compile && mvn exec:java \
                -Dexec.mainClass="com.you.nbody.Main"


