FROM ubuntu:19.10

RUN if [ ${OSTYPE} = Linux ] && [ ${USER_ID:-0} -ne 0 ] && [ ${GROUP_ID:-0} -ne 0 ]; then \
    groupadd -g ${GROUP_ID} algorand &&\
    useradd -l -u ${USER_ID} -g algorand algorand &&\
    chown --changes --silent --no-dereference --recursive \
          --from=33:33 ${USER_ID}:${GROUP_ID} \
        /opt \
;fi

ENV DEBIAN_FRONTEND noninteractive

# Basic dependencies
ENV HOME /opt
RUN apt-get update && apt-get install -y apt-utils curl git git-core bsdmainutils

# Install python dependencies
ENV PYENV_ROOT $HOME/pyenv
ENV PATH $PYENV_ROOT/bin:$PATH
RUN apt-get install -y curl gcc make zlib1g-dev libbz2-dev libreadline-dev libsqlite3-dev libssl-dev libffi-dev
RUN git clone https://github.com/pyenv/pyenv.git $HOME/pyenv

RUN eval "$(pyenv init -)" && \
    pyenv install 3.7.1 && \
    pyenv global 3.7.1 && \
    pip install --upgrade pip && \
    pyenv rehash
ENV PATH=$PYENV_ROOT/shims:$PATH

# Install Java dependencies
RUN apt-get install -y maven default-jdk

# Install Go dependencies
ARG GOLANG_VERSION=1.13.8
RUN curl https://dl.google.com/go/go${GOLANG_VERSION}.linux-amd64.tar.gz -o $HOME/go.tar.gz
RUN tar -xvf $HOME/go.tar.gz -C /usr/local
ENV GOROOT /usr/local/go
ENV GOPATH $HOME/go
ENV PATH $GOROOT/bin:$PATH

# Install algorand-sdk-testing script dependencies
RUN pip3 install gitpython

RUN mkdir -p $HOME/java-algorand-sdk
WORKDIR $HOME/java-algorand-sdk
CMD ["/bin/bash", "-c", "GO111MODULE=off && temp/docker/setup.py --algod-config temp/config_future && temp/docker/test.py --algod-config temp/config_future --network-dir /opt/testnetwork"]
