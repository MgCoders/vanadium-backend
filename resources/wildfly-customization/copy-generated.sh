#!/usr/bin/env bash
#/bin/bash
set -x
mkdir /tmp/generated
cd /opt/jboss/wildfly/standalone
tar -zcvf /tmp/generated/wildfly-conf.tar.gz configuration
cd /opt/jboss/wildfly/modules/
tar -zcvf /tmp/generated/postgresql-42.1.4.module.tar.gz org