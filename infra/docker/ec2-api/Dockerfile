FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y \
    openssh-server \
    sudo \
    python3 \
    ca-certificates \
    curl \
    docker.io \
    && rm -rf /var/lib/apt/lists/*

RUN useradd -m -s /bin/bash ubuntu \
    && echo "ubuntu:vocealuga" | chpasswd \
    && usermod -aG sudo ubuntu \
    && usermod -aG docker ubuntu \
    && echo "Defaults:ubuntu !requiretty" > /etc/sudoers.d/ubuntu-voce-aluga \
    && echo "ubuntu ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers.d/ubuntu-voce-aluga \
    && chmod 440 /etc/sudoers.d/ubuntu-voce-aluga

RUN mkdir -p /var/run/sshd \
    && sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin no/' /etc/ssh/sshd_config \
    && sed -i 's/#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config \
    && printf '%s\n' 'PubkeyAuthentication yes' 'PasswordAuthentication yes' 'UseDNS no' \
      > /etc/ssh/sshd_config.d/99-voce-aluga.conf

EXPOSE 22 8081

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

CMD ["/entrypoint.sh"]
