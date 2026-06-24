#!/bin/bash
set -e

mkdir -p /home/ubuntu/.ssh
chmod 700 /home/ubuntu/.ssh

if [ -f /run/keys/authorized_keys ]; then
  cp /run/keys/authorized_keys /home/ubuntu/.ssh/authorized_keys
  chown ubuntu:ubuntu /home/ubuntu/.ssh/authorized_keys
  chmod 600 /home/ubuntu/.ssh/authorized_keys
fi

/usr/sbin/sshd -D
