
FROM ubuntu:16.04
MAINTAINER Simon Meggle "simon.meggle@consol.de"
ENV REFRESHED_AT 2016-12-13

ARG SOURCE_BRANCH
ARG SOURCE_TAG
ARG SOURCE_COMMIT

ENV BRANCH=${SOURCE_BRANCH}
RUN env > /root/env

RUN echo "Source-Branch = ${SOURCE_BRANCH:-nullAhnung}" > /root/build_info

WORKDIR /root
