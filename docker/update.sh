#!/bin/bash
mkdir old_version
git archive --remote=.. --format=tgz master | tar zxf - -C old_version --exclude docker/*
cd old_version
tar zcf ../old_version.tgz *
cd ..
rm -Rf old_version
