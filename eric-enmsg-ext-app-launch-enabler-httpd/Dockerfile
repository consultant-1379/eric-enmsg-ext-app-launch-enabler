ARG ERIC_ENM_SLES_APACHE2_IMAGE_NAME=eric-enm-sles-apache2
ARG ERIC_ENM_SLES_APACHE2_IMAGE_REPO=armdocker.rnd.ericsson.se/proj-enm
ARG ERIC_ENM_SLES_APACHE2_IMAGE_TAG=1.59.0-33

FROM ${ERIC_ENM_SLES_APACHE2_IMAGE_REPO}/${ERIC_ENM_SLES_APACHE2_IMAGE_NAME}:${ERIC_ENM_SLES_APACHE2_IMAGE_TAG}

ARG BUILD_DATE=unspecified
ARG GIT_COMMIT=unspecified
ARG IMAGE_BUILD_VERSION=unspecified
ARG ISO_VERSION=unspecified
ARG RSTATE=unspecified
ARG SGUSER=250252

LABEL com.ericsson.product-number="CXC Placeholder" \
      com.ericsson.product-revision=$RSTATE \
      enm_iso_version=$ISO_VERSION \
      org.label-schema.build-date=$BUILD_DATE \
      org.label-schema.name="ENM ext-app-launch-enabler Service Group SideCar" \
      org.label-schema.schema-version="1.0.0-rc1" \
      org.label-schema.vcs-ref=$GIT_COMMIT \
      org.label-schema.vendor="Ericsson" \
      org.label-schema.version=$IMAGE_BUILD_VERSION

ENV PROXY_PASS_RULES="eale"
ENV CREDM_CONTROLLER_MNG="TRUE"

RUN echo "$SGUSER:x:$SGUSER:0:An Identity for ext-app-launch-enabler-httpd:/nonexistent:/bin/false" >>/etc/passwd && \
    echo "$SGUSER:!::0:::::" >>/etc/shadow

EXPOSE 8084 8444

USER $SGUSER
